package com.veneca.app.ui

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.veneca.app.data.AuthRepository
import com.veneca.app.data.ExchangeRateService
import com.veneca.app.data.MarketplaceRepository
import com.veneca.app.data.OrderRepository
import com.veneca.app.data.SettingsRepository
import com.veneca.app.model.AppThemeMode
import com.veneca.app.model.CurrencyDisplayMode
import com.veneca.app.model.LiveExchangeRates
import com.veneca.app.model.P2POrder
import com.veneca.app.model.Product
import com.veneca.app.model.User
import com.veneca.app.model.UserRole
import com.veneca.app.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    TERMS,
    AUTH,
    DASHBOARD
}

@Immutable
data class MarketplaceUiState(
    val currentScreen: AppScreen = AppScreen.TERMS,
    val currentUser: User,
    val allUsers: List<User> = emptyList(),
    val query: String = "",
    val selectedCategory: String = "Todos",
    val filterType: String = "Todos", // "Todos", "iaCuotas", "Mayor", "Detal"
    val products: List<Product> = emptyList(),
    val orders: List<P2POrder> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val liveRates: LiveExchangeRates = LiveExchangeRates(),
    val isAccountModalOpen: Boolean = false,
    val isSettingsModalOpen: Boolean = false,
    val message: String? = null,
    val selectedProductForPurchase: Product? = null
) {
    val bcvRate: Double
        get() = liveRates.bcvRate.takeIf { it > 0 } ?: settings.customBcvRate

    val usdtRate: Double
        get() = liveRates.usdtRate.takeIf { it > 0 } ?: 1066.78
}

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(application)
    private val marketRepo = MarketplaceRepository(application)
    private val orderRepo = OrderRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val exchangeRateService = ExchangeRateService()

    private val initialScreen: AppScreen = when {
        !settingsRepo.hasAcceptedTerms() -> AppScreen.TERMS
        !settingsRepo.isUserLoggedIn() -> AppScreen.AUTH
        else -> AppScreen.DASHBOARD
    }

    private val _uiState = MutableStateFlow(
        MarketplaceUiState(
            currentScreen = initialScreen,
            currentUser = authRepo.getCurrentUser(),
            allUsers = authRepo.getAllUsers(),
            products = marketRepo.getAllProducts(),
            orders = orderRepo.getAllOrders(),
            settings = settingsRepo.getSettings(),
            liveRates = LiveExchangeRates(
                bcvRate = settingsRepo.getSettings().customBcvRate,
                usdtRate = 1066.78
            )
        )
    )
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        // Automatically fetch live official BCV & USDT rates on startup
        refreshExchangeRates()
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                liveRates = _uiState.value.liveRates.copy(isLoading = true)
            )
            val updated = exchangeRateService.fetchLiveRates()
            _uiState.value = _uiState.value.copy(
                liveRates = updated,
                message = "Tasas actualizadas: BCV Bs. ${"%.2f".format(updated.bcvRate)} · USDT Bs. ${"%.2f".format(updated.usdtRate)}"
            )
        }
    }

    // Navigation and Onboarding
    fun acceptTerms() {
        settingsRepo.setAcceptedTerms(true)
        _uiState.value = _uiState.value.copy(
            currentScreen = if (settingsRepo.isUserLoggedIn()) AppScreen.DASHBOARD else AppScreen.AUTH,
            message = "Términos y condiciones aceptados"
        )
    }

    fun viewTermsScreen() {
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.TERMS,
            isSettingsModalOpen = false,
            isAccountModalOpen = false
        )
    }

    fun logout() {
        settingsRepo.setUserLoggedIn(false)
        _uiState.value = _uiState.value.copy(
            currentScreen = AppScreen.AUTH,
            isAccountModalOpen = false,
            isSettingsModalOpen = false,
            message = "Sesión cerrada"
        )
    }

    // Search and Filters
    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun onFilterTypeSelected(filter: String) {
        _uiState.value = _uiState.value.copy(filterType = filter)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.query
        val category = _uiState.value.selectedCategory
        val filterType = _uiState.value.filterType

        val wholesaleOnly = filterType == "Mayor"
        var list = marketRepo.searchProducts(query, category, wholesaleOnly)
        when (filterType) {
            "Detal" -> list = list.filter { it.wholesalePrice == null }
            "iaCuotas" -> list = list.filter { it.iaCuotasEligible }
        }
        _uiState.value = _uiState.value.copy(products = list)
    }

    // Modal Visibility Controls
    fun openAccountModal() {
        _uiState.value = _uiState.value.copy(isAccountModalOpen = true)
    }

    fun closeAccountModal() {
        _uiState.value = _uiState.value.copy(isAccountModalOpen = false)
    }

    fun openSettingsModal() {
        _uiState.value = _uiState.value.copy(isSettingsModalOpen = true)
    }

    fun closeSettingsModal() {
        _uiState.value = _uiState.value.copy(isSettingsModalOpen = false)
    }

    // Settings Updates
    fun updateThemeMode(theme: AppThemeMode) {
        val updated = _uiState.value.settings.copy(themeMode = theme)
        settingsRepo.saveSettings(updated)
        _uiState.value = _uiState.value.copy(settings = updated)
    }

    fun updateCurrencyMode(mode: CurrencyDisplayMode) {
        val updated = _uiState.value.settings.copy(currencyMode = mode)
        settingsRepo.saveSettings(updated)
        _uiState.value = _uiState.value.copy(settings = updated)
    }

    fun updateCustomBcvRate(rate: Double) {
        if (rate > 0) {
            val updated = _uiState.value.settings.copy(customBcvRate = rate)
            settingsRepo.saveSettings(updated)
            _uiState.value = _uiState.value.copy(
                settings = updated,
                liveRates = _uiState.value.liveRates.copy(bcvRate = rate),
                message = "Tasa BCV configurada en Bs. ${"%.2f".format(rate)}"
            )
        }
    }

    fun toggleHighPerformanceMode(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(highPerformanceMode = enabled)
        settingsRepo.saveSettings(updated)
        _uiState.value = _uiState.value.copy(
            settings = updated,
            message = if (enabled) "Modo Alto Rendimiento activado (Sin Lag)" else "Modo Alto Rendimiento desactivado"
        )
    }

    fun toggleIaCuotasBadges(enabled: Boolean) {
        val updated = _uiState.value.settings.copy(showIaCuotasBadges = enabled)
        settingsRepo.saveSettings(updated)
        _uiState.value = _uiState.value.copy(settings = updated)
    }

    fun clearAllUserData() {
        marketRepo.clearAllProducts()
        orderRepo.clearAllOrders()
        _uiState.value = _uiState.value.copy(
            products = emptyList(),
            orders = emptyList(),
            message = "Catálogo y órdenes limpiados"
        )
    }

    // Auth & User Management
    fun register(
        name: String,
        email: String,
        cedula: String,
        phone: String,
        role: UserRole,
        businessName: String?,
        pagoMovilData: String?,
        binanceId: String?
    ) {
        val result = authRepo.register(
            name, email, cedula, phone, role, businessName, pagoMovilData, binanceId
        )
        result.onSuccess { user ->
            settingsRepo.setUserLoggedIn(true)
            _uiState.value = _uiState.value.copy(
                currentUser = user,
                allUsers = authRepo.getAllUsers(),
                currentScreen = AppScreen.DASHBOARD,
                message = "Bienvenido a iavnzla, ${user.name}"
            )
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(message = err.message ?: "Error al registrar")
        }
    }

    fun login(emailOrCedula: String) {
        val result = authRepo.login(emailOrCedula)
        result.onSuccess { user ->
            settingsRepo.setUserLoggedIn(true)
            _uiState.value = _uiState.value.copy(
                currentUser = user,
                allUsers = authRepo.getAllUsers(),
                currentScreen = AppScreen.DASHBOARD,
                message = "Sesión iniciada como ${user.name}"
            )
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(message = err.message ?: "Error al iniciar sesión")
        }
    }

    fun switchActiveUser(userId: String) {
        val user = authRepo.switchUser(userId)
        if (user != null) {
            settingsRepo.setUserLoggedIn(true)
            _uiState.value = _uiState.value.copy(
                currentUser = user,
                currentScreen = AppScreen.DASHBOARD,
                message = "Cuenta activa: ${user.name} (${user.role.label})"
            )
        }
    }

    fun selectProductForPurchase(product: Product?) {
        _uiState.value = _uiState.value.copy(selectedProductForPurchase = product)
    }

    fun createOrder(
        product: Product,
        quantity: Int,
        paymentMethod: String
    ): P2POrder? {
        val user = _uiState.value.currentUser
        val isWholesale = product.wholesalePrice != null && quantity >= product.wholesaleMinimum
        val unitPrice = if (isWholesale) product.wholesalePrice!! else product.price
        val totalUsd = unitPrice * quantity
        val bcvRate = _uiState.value.bcvRate
        val totalVes = totalUsd * bcvRate

        val isIaCuotas = paymentMethod.contains("iaCuotas", ignoreCase = true)
        val downPaymentUsd = if (isIaCuotas) product.calculateDownPayment(totalUsd) else 0.0
        val installmentUsd = if (isIaCuotas) product.calculateInstallmentAmount(totalUsd) else 0.0

        val paymentDetails = when {
            isIaCuotas -> "iaCuotas iavnzla · Pago inicial de $${"%.2f".format(downPaymentUsd)} y 3 cuotas quincenales de $${"%.2f".format(installmentUsd)}"
            paymentMethod == "Binance Pay" -> product.sellerBinanceId
            else -> product.sellerPagoMovilData
        }

        val order = orderRepo.createOrder(
            productId = product.id,
            productName = product.name,
            buyerId = user.id,
            buyerName = user.name,
            sellerId = product.sellerId,
            sellerName = product.sellerName,
            quantity = quantity,
            unitPriceUsd = unitPrice,
            totalUsd = totalUsd,
            totalVes = totalVes,
            isWholesale = isWholesale,
            paymentMethod = paymentMethod,
            sellerPaymentDetails = paymentDetails,
            isIaCuotasOrder = isIaCuotas,
            iaDownPaymentUsd = downPaymentUsd,
            iaInstallmentAmountUsd = installmentUsd
        )

        // Deduct from user's credit line if iaCuotas
        if (isIaCuotas) {
            val updatedUser = user.copy(
                iaCreditUsed = user.iaCreditUsed + totalUsd
            )
            authRepo.saveUsers(authRepo.getAllUsers().map { if (it.id == user.id) updatedUser else it })
            _uiState.value = _uiState.value.copy(currentUser = updatedUser)
        }

        _uiState.value = _uiState.value.copy(
            orders = orderRepo.getAllOrders(),
            selectedProductForPurchase = null,
            message = if (isIaCuotas) "¡Compra con iaCuotas aprobada! Inicial: $${"%.2f".format(downPaymentUsd)}" else "Orden P2P #${order.id} creada."
        )
        return order
    }

    fun submitBuyerPayment(orderId: String, paymentRefId: String) {
        val result = orderRepo.buyerSubmitPaymentRef(orderId, paymentRefId)
        result.onSuccess { updated ->
            _uiState.value = _uiState.value.copy(
                orders = orderRepo.getAllOrders(),
                message = "Pago notificado al vendedor con ID: ${updated.buyerPaymentRef}. Esperando verificación."
            )
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(message = err.message)
        }
    }

    fun submitSellerConfirmation(orderId: String, verificationRefId: String) {
        val result = orderRepo.sellerConfirmPayment(orderId, verificationRefId)
        result.onSuccess { updated ->
            _uiState.value = _uiState.value.copy(
                orders = orderRepo.getAllOrders(),
                message = "¡Pago confirmado por el vendedor! Orden completada con éxito."
            )
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(message = err.message)
        }
    }

    fun cancelOrder(orderId: String) {
        val result = orderRepo.cancelOrder(orderId)
        result.onSuccess {
            _uiState.value = _uiState.value.copy(
                orders = orderRepo.getAllOrders(),
                message = "Orden cancelada"
            )
        }.onFailure { err ->
            _uiState.value = _uiState.value.copy(message = err.message)
        }
    }

    fun addProduct(
        name: String,
        category: String,
        description: String,
        unit: String,
        retailPrice: String,
        wholesalePrice: String,
        wholesaleMinimum: String,
        stock: String,
        pagoMovilData: String,
        binanceId: String,
        iaCuotasEligible: Boolean = true
    ) {
        val retail = retailPrice.toDoubleOrNull()
        val wholesale = wholesalePrice.takeIf(String::isNotBlank)?.toDoubleOrNull()
        val minimum = wholesaleMinimum.toIntOrNull() ?: 1
        val quantity = stock.toIntOrNull()

        if (retail == null || (wholesalePrice.isNotBlank() && wholesale == null) || quantity == null) {
            _uiState.value = _uiState.value.copy(message = "Revisa los precios y la cantidad.")
            return
        }

        val user = _uiState.value.currentUser

        try {
            marketRepo.addProduct(
                name = name,
                category = category,
                description = description,
                unit = unit,
                retailPrice = retail,
                wholesalePrice = wholesale,
                wholesaleMinimum = minimum,
                stock = quantity,
                sellerId = user.id,
                sellerName = user.businessName ?: user.name,
                sellerPhone = user.phone,
                sellerPaymentMethods = listOf("iaCuotas", "Pago Móvil", "Binance Pay"),
                sellerPagoMovilData = pagoMovilData.ifBlank { user.pagoMovilData ?: "Pago Móvil disponible" },
                sellerBinanceId = binanceId.ifBlank { user.binanceId ?: "Binance Pay disponible" },
                iaCuotasEligible = iaCuotasEligible
            )
            applyFilters()
            _uiState.value = _uiState.value.copy(message = "¡Artículo publicado con éxito en iavnzla!")
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(message = error.message)
        }
    }

    fun deleteProduct(productId: String) {
        if (marketRepo.deleteProduct(productId)) {
            applyFilters()
            _uiState.value = _uiState.value.copy(message = "Artículo eliminado")
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
