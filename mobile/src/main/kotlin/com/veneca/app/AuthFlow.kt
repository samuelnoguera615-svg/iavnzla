package com.veneca.app

import java.time.Clock
import java.time.LocalDate
import java.time.Period

data class GoogleIdentity(
    val userId: String,
    val email: String,
    val emailVerified: Boolean,
    val displayName: String
)

data class AuthProfile(
    val userId: String,
    val email: String,
    val fullName: String,
    val birthDate: LocalDate,
    val status: AccountStatus,
    val guardianEmail: String? = null,
    val guardianApproved: Boolean = false
)

enum class AccountStatus {
    ACTIVE,
    PENDING_GUARDIAN,
    SUSPENDED
}

sealed class AuthResult {
    data class NeedsRegistration(val identity: GoogleIdentity) : AuthResult()
    data class WaitingForFamily(val guardianEmail: String) : AuthResult()
    data class SignedIn(val profile: AuthProfile) : AuthResult()
    data class AccessBlocked(val reason: String) : AuthResult()
}

interface GoogleAuthGateway {
    /** Return only the account confirmed by Supabase Auth after Google OAuth completes. */
    fun signIn(): GoogleIdentity
}

interface AuthRepository {
    fun findProfile(userId: String): AuthProfile?

    /**
     * Must send the identity number over TLS and persist only a server-keyed HMAC,
     * never the plain number. A minor profile and its approval request are one transaction.
     */
    fun register(
        identity: GoogleIdentity,
        fullName: String,
        identityNumber: String,
        birthDate: LocalDate,
        guardianEmail: String?
    ): AuthProfile

    /** The server must independently enforce guardian email, age, and pending-request checks. */
    fun approveMinorAccess(guardianUserId: String, minorUserId: String)
}

class AuthFlow(
    private val googleAuth: GoogleAuthGateway,
    private val repository: AuthRepository,
    private val clock: Clock = Clock.systemDefaultZone()
) {
    fun signIn(): AuthResult {
        val identity = verifiedGoogleIdentity()
        val profile = repository.findProfile(identity.userId)
            ?: return AuthResult.NeedsRegistration(identity)

        if (profile.status == AccountStatus.PENDING_GUARDIAN) {
            return AuthResult.WaitingForFamily(profile.guardianEmail.orEmpty())
        }
        if (profile.status != AccountStatus.ACTIVE) {
            return AuthResult.AccessBlocked("La cuenta no está habilitada.")
        }
        if (ageOn(profile.birthDate, LocalDate.now(clock)) < MINIMUM_AGE_WITHOUT_APPROVAL &&
            !profile.guardianApproved
        ) {
            return AuthResult.AccessBlocked("Esta cuenta requiere aprobación familiar.")
        }
        return AuthResult.SignedIn(profile)
    }

    fun register(
        fullName: String,
        identityNumber: String,
        birthDate: LocalDate,
        guardianEmail: String? = null
    ): AuthResult {
        val identity = verifiedGoogleIdentity()
        require(repository.findProfile(identity.userId) == null) {
            "La cuenta ya está registrada."
        }
        require(fullName.trim().length >= 2) { "Ingresa tu nombre completo." }
        normalizeIdentityNumber(identityNumber)

        val today = LocalDate.now(clock)
        require(!birthDate.isAfter(today)) { "La fecha de nacimiento no puede ser futura." }

        val needsGuardian = ageOn(birthDate, today) < MINIMUM_AGE_WITHOUT_APPROVAL
        val normalizedGuardianEmail = guardianEmail?.trim()?.lowercase()
        if (needsGuardian) {
            require(!normalizedGuardianEmail.isNullOrBlank()) {
                "Debes indicar el correo de un familiar adulto registrado."
            }
            require(normalizedGuardianEmail != identity.email.lowercase()) {
                "El familiar debe usar otra cuenta."
            }
        }

        val profile = repository.register(
            identity = identity,
            fullName = fullName.trim(),
            identityNumber = normalizeIdentityNumber(identityNumber),
            birthDate = birthDate,
            guardianEmail = if (needsGuardian) normalizedGuardianEmail else null
        )
        return if (needsGuardian) {
            AuthResult.WaitingForFamily(profile.guardianEmail.orEmpty())
        } else if (profile.status == AccountStatus.ACTIVE) {
            AuthResult.SignedIn(profile)
        } else {
            AuthResult.AccessBlocked("La cuenta no quedó habilitada; contacta soporte.")
        }
    }

    fun approveFamilyMember(minorUserId: String) {
        val guardian = verifiedGoogleIdentity()
        val profile = repository.findProfile(guardian.userId)
            ?: throw IllegalStateException("El familiar debe tener una cuenta registrada.")
        require(ageOn(profile.birthDate, LocalDate.now(clock)) >= ADULT_AGE) {
            "Solo una persona adulta puede aprobar el acceso."
        }
        repository.approveMinorAccess(guardian.userId, minorUserId)
    }

    private fun verifiedGoogleIdentity(): GoogleIdentity {
        val identity = googleAuth.signIn()
        require(identity.userId.isNotBlank() && identity.email.isNotBlank() && identity.emailVerified) {
            "Inicia sesión con una cuenta de Google verificada."
        }
        return identity.copy(email = identity.email.trim().lowercase())
    }

    private fun normalizeIdentityNumber(value: String): String {
        val normalized = value.trim().uppercase().replace(" ", "").replace("-", "")
        require(IDENTITY_NUMBER.matches(normalized)) {
            "La cédula debe tener V o E y entre 6 y 9 dígitos."
        }
        return normalized
    }

    private fun ageOn(birthDate: LocalDate, date: LocalDate): Int =
        Period.between(birthDate, date).years

    companion object {
        private const val MINIMUM_AGE_WITHOUT_APPROVAL = 17
        private const val ADULT_AGE = 18
        private val IDENTITY_NUMBER = Regex("^[VE]\\d{6,9}$")
    }
}
