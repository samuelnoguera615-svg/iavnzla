package com.veneca.app.data

import android.content.Context
import com.veneca.app.model.SellerTier
import com.veneca.app.model.User
import com.veneca.app.model.UserRole
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AuthRepository(context: Context) {
    private val preferences = context.getSharedPreferences("iavnzla_auth_clean_v5", Context.MODE_PRIVATE)

    // User requested: 0 clientes y 0 empresas precargadas.
    // La app inicia completamente vacía y los usuarios se registran ellos mismos.

    fun getAllUsers(): List<User> {
        val raw = preferences.getString(KEY_USERS, null) ?: return emptyList()
        val array = JSONArray(raw)
        return (0 until array.length()).map { idx ->
            val obj = array.getJSONObject(idx)
            User(
                id = obj.getString("id"),
                name = obj.getString("name"),
                email = obj.getString("email"),
                cedula = obj.optString("cedula", "V-00000000"),
                phone = obj.optString("phone", "0414-0000000"),
                role = UserRole.valueOf(obj.optString("role", UserRole.BUYER.name)),
                tier = SellerTier.valueOf(obj.optString("tier", SellerTier.BRONZE.name)),
                businessName = obj.optString("businessName", "").ifBlank { null },
                pagoMovilData = obj.optString("pagoMovilData", "").ifBlank { null },
                binanceId = obj.optString("binanceId", "").ifBlank { null },
                rating = obj.optDouble("rating", 5.0),
                totalOrders = obj.optInt("totalOrders", 0),
                iaVnzlaLevel = obj.optInt("iaVnzlaLevel", 1),
                iaCreditLimit = obj.optDouble("iaCreditLimit", 200.0),
                iaCreditUsed = obj.optDouble("iaCreditUsed", 0.0)
            )
        }
    }

    fun saveUsers(users: List<User>) {
        val array = JSONArray()
        users.forEach { u ->
            array.put(JSONObject().apply {
                put("id", u.id)
                put("name", u.name)
                put("email", u.email)
                put("cedula", u.cedula)
                put("phone", u.phone)
                put("role", u.role.name)
                put("tier", u.tier.name)
                put("businessName", u.businessName ?: "")
                put("pagoMovilData", u.pagoMovilData ?: "")
                put("binanceId", u.binanceId ?: "")
                put("rating", u.rating)
                put("totalOrders", u.totalOrders)
                put("iaVnzlaLevel", u.iaVnzlaLevel)
                put("iaCreditLimit", u.iaCreditLimit)
                put("iaCreditUsed", u.iaCreditUsed)
            })
        }
        preferences.edit().putString(KEY_USERS, array.toString()).apply()
    }

    fun getActiveUserId(): String? {
        return preferences.getString(KEY_ACTIVE_USER_ID, null)
    }

    fun setActiveUserId(id: String?) {
        preferences.edit().putString(KEY_ACTIVE_USER_ID, id).apply()
    }

    fun getCurrentUser(): User {
        val activeId = getActiveUserId()
        val all = getAllUsers()
        return all.find { it.id == activeId } ?: all.firstOrNull() ?: User(
            id = "guest",
            name = "Nuevo Usuario",
            email = "usuario@iavnzla.ve",
            cedula = "V-00000000",
            phone = "0400-0000000",
            role = UserRole.BUYER,
            iaVnzlaLevel = 1,
            iaCreditLimit = 200.0,
            iaCreditUsed = 0.0
        )
    }

    fun register(
        name: String,
        email: String,
        cedula: String,
        phone: String,
        role: UserRole,
        businessName: String?,
        pagoMovilData: String?,
        binanceId: String?
    ): Result<User> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = name.trim()
        val trimmedCedula = cedula.trim()
        val trimmedPhone = phone.trim()

        if (trimmedName.isBlank()) return Result.failure(IllegalArgumentException("El nombre es requerido"))
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Ingresa un correo electrónico válido"))
        }
        if (trimmedCedula.isBlank()) return Result.failure(IllegalArgumentException("La cédula o RIF es requerida"))
        if (trimmedPhone.isBlank()) return Result.failure(IllegalArgumentException("El número de WhatsApp o teléfono es requerido"))

        val users = getAllUsers().toMutableList()
        if (users.any { it.email.lowercase() == trimmedEmail }) {
            return Result.failure(IllegalArgumentException("El correo ya se encuentra registrado"))
        }

        val newUser = User(
            id = UUID.randomUUID().toString().take(12),
            name = trimmedName,
            email = trimmedEmail,
            cedula = trimmedCedula,
            phone = trimmedPhone,
            role = role,
            tier = if (role == UserRole.SELLER_WHOLESALE) SellerTier.SILVER else SellerTier.BRONZE,
            businessName = businessName?.trim()?.ifBlank { null },
            pagoMovilData = pagoMovilData?.trim()?.ifBlank { null },
            binanceId = binanceId?.trim()?.ifBlank { null },
            iaVnzlaLevel = if (role == UserRole.SELLER_WHOLESALE) 2 else 1,
            iaCreditLimit = if (role == UserRole.SELLER_WHOLESALE) 400.0 else 200.0,
            iaCreditUsed = 0.0
        )

        users.add(newUser)
        saveUsers(users)
        setActiveUserId(newUser.id)
        return Result.success(newUser)
    }

    fun login(emailOrCedula: String): Result<User> {
        val normalized = emailOrCedula.trim().lowercase()
        if (normalized.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa tu correo o cédula"))
        }
        val user = getAllUsers().find {
            it.email.lowercase() == normalized || it.cedula.lowercase() == normalized
        } ?: return Result.failure(IllegalArgumentException("No existe ninguna cuenta registrada con '$emailOrCedula'. Por favor, ve a la pestaña 'Crear Cuenta' para registrarte."))

        setActiveUserId(user.id)
        return Result.success(user)
    }

    fun switchUser(userId: String): User? {
        val user = getAllUsers().find { it.id == userId }
        if (user != null) {
            setActiveUserId(user.id)
        }
        return user
    }

    fun logout() {
        setActiveUserId(null)
    }

    companion object {
        private const val KEY_USERS = "iavnzla_clean_users_list_v5"
        private const val KEY_ACTIVE_USER_ID = "iavnzla_active_user_clean_v5"
    }
}
