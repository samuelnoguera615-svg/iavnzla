CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT,
    birth_date DATE,
    identity_number_hmac BYTEA,
    account_status TEXT NOT NULL DEFAULT 'active'
        CHECK (account_status IN ('active', 'pending_guardian', 'suspended')),
    guardian_approved BOOLEAN NOT NULL DEFAULT FALSE,
    business_name TEXT,
    is_seller BOOLEAN DEFAULT FALSE,
    seller_code TEXT UNIQUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE profiles ADD COLUMN IF NOT EXISTS email TEXT;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS birth_date DATE;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS identity_number_hmac BYTEA;
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS account_status TEXT NOT NULL DEFAULT 'active';
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS guardian_approved BOOLEAN NOT NULL DEFAULT FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS idx_profiles_email_lower
    ON profiles(LOWER(email))
    WHERE email IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_profiles_identity_number_hmac
    ON profiles(identity_number_hmac)
    WHERE identity_number_hmac IS NOT NULL;

CREATE TABLE IF NOT EXISTS guardian_approval_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    minor_user_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    guardian_email TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending'
        CHECK (status IN ('pending', 'approved', 'rejected', 'expired')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    decided_at TIMESTAMPTZ,
    decided_by UUID REFERENCES profiles(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_guardian_requests_one_pending_per_minor
    ON guardian_approval_requests(minor_user_id)
    WHERE status = 'pending';
CREATE INDEX IF NOT EXISTS idx_guardian_requests_email_pending
    ON guardian_approval_requests(LOWER(guardian_email))
    WHERE status = 'pending';

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE guardian_approval_requests ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS profiles_read_own ON profiles;
CREATE POLICY profiles_read_own ON profiles
    FOR SELECT TO authenticated
    USING (id = auth.uid());

DROP POLICY IF EXISTS guardian_requests_read_involved ON guardian_approval_requests;
CREATE POLICY guardian_requests_read_involved ON guardian_approval_requests
    FOR SELECT TO authenticated
    USING (
        minor_user_id = auth.uid()
        OR LOWER(guardian_email) = LOWER(COALESCE(auth.jwt() ->> 'email', ''))
    );

CREATE OR REPLACE FUNCTION approve_guardian_request(request_id UUID)
RETURNS VOID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    current_user_id UUID := auth.uid();
    current_email TEXT := LOWER(COALESCE(auth.jwt() ->> 'email', ''));
    guardian_birth_date DATE;
    minor_id UUID;
    minor_birth_date DATE;
    request_email TEXT;
BEGIN
    IF current_user_id IS NULL OR current_email = '' THEN
        RAISE EXCEPTION 'Debe iniciar sesión con una cuenta verificada';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM auth.users
        WHERE id = current_user_id
          AND LOWER(email) = current_email
          AND email_confirmed_at IS NOT NULL
    ) THEN
        RAISE EXCEPTION 'Debe iniciar sesión con un correo verificado';
    END IF;

    SELECT birth_date INTO guardian_birth_date
    FROM profiles
    WHERE id = current_user_id
      AND account_status = 'active'
      AND LOWER(email) = current_email;

    IF guardian_birth_date IS NULL
       OR DATE_PART('year', AGE(CURRENT_DATE, guardian_birth_date)) < 18 THEN
        RAISE EXCEPTION 'Solo una persona adulta con perfil activo puede aprobar';
    END IF;

    SELECT minor_user_id, guardian_email
    INTO minor_id, request_email
    FROM guardian_approval_requests
    WHERE id = request_id AND status = 'pending'
    FOR UPDATE;

    IF minor_id IS NULL
       OR minor_id = current_user_id
       OR LOWER(request_email) <> current_email THEN
        RAISE EXCEPTION 'Solicitud inexistente o dirigida a otro correo';
    END IF;

    SELECT birth_date INTO minor_birth_date
    FROM profiles
    WHERE id = minor_id AND account_status = 'pending_guardian';

    IF minor_birth_date IS NULL
       OR DATE_PART('year', AGE(CURRENT_DATE, minor_birth_date)) >= 17 THEN
        RAISE EXCEPTION 'La solicitud ya no requiere aprobación familiar';
    END IF;

    UPDATE guardian_approval_requests
    SET status = 'approved', decided_at = NOW(), decided_by = current_user_id
    WHERE id = request_id;

    UPDATE profiles
    SET account_status = 'active', guardian_approved = TRUE
    WHERE id = minor_id;
END;
$$;

REVOKE ALL ON FUNCTION approve_guardian_request(UUID) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION approve_guardian_request(UUID) TO authenticated;

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT,
    stock INTEGER DEFAULT 0,
    retail_price NUMERIC(12,2) DEFAULT 0,
    wholesale_price NUMERIC(12,2) DEFAULT 0,
    seller_id UUID REFERENCES profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sales (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES products(id),
    seller_id UUID REFERENCES profiles(id),
    buyer_code TEXT,
    vendor_code TEXT NOT NULL,
    sale_code TEXT NOT NULL UNIQUE,
    quantity INTEGER DEFAULT 1,
    total_amount NUMERIC(12,2) DEFAULT 0,
    status TEXT DEFAULT 'pending',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS seller_levels (
    id UUID PRIMARY KEY,
    seller_id UUID REFERENCES profiles(id),
    level_name TEXT NOT NULL,
    total_sales INTEGER DEFAULT 0,
    monthly_volume NUMERIC(12,2) DEFAULT 0,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS privacy_consents (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES profiles(id),
    accepted_privacy BOOLEAN DEFAULT FALSE,
    accepted_terms BOOLEAN DEFAULT FALSE,
    accepted_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_sales_vendor_code ON sales(vendor_code);
CREATE INDEX IF NOT EXISTS idx_sales_sale_code ON sales(sale_code);
