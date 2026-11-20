package com.veneca.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.veneca.app.model.OrderStatus
import com.veneca.app.model.Product
import com.veneca.app.model.UserRole
import com.veneca.app.ui.AppScreen
import com.veneca.app.ui.MarketplaceViewModel
import com.veneca.app.ui.components.*
import com.veneca.app.ui.theme.*

private enum class MarketTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SHOP("Mercado", Icons.Default.ShoppingBag),
    ORDERS("Órdenes P2P", Icons.Default.AccountBalanceWallet),
    PUBLISH("Publicar", Icons.Default.AddCircle),
    ACCOUNT("Mi Cuenta", Icons.Default.Person)
}

@Composable
fun MarketplaceScreen(viewModel: MarketplaceViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(MarketTab.SHOP) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Step 1: Check screen routing (Términos -> Auth -> Dashboard)
    when (state.currentScreen) {
        AppScreen.TERMS -> {
            TermsScreen(
                onAcceptTerms = { viewModel.acceptTerms() }
            )
        }
        AppScreen.AUTH -> {
            AuthScreen(
                allUsers = state.allUsers,
                onLogin = viewModel::login,
                onRegister = viewModel::register,
                onQuickSwitchUser = viewModel::switchActiveUser
            )
        }
        AppScreen.DASHBOARD -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(snackbar) },
                topBar = {
                    VenecaTopHeader(
                        currentUser = state.currentUser,
                        allUsers = state.allUsers,
                        liveRates = state.liveRates,
                        onOpenAccount = { viewModel.openAccountModal() },
                        onOpenSettings = { viewModel.openSettingsModal() },
                        onRefreshRates = { viewModel.refreshExchangeRates() }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        MarketTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            val pendingCount = if (tab == MarketTab.ORDERS) {
                                state.orders.count { it.status == OrderStatus.PENDING_PAYMENT || it.status == OrderStatus.PAID_PENDING_SELLER }
                            } else 0

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (pendingCount > 0) {
                                                Badge(containerColor = VenecaAmber) {
                                                    Text(pendingCount.toString(), color = Color.Black, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            tint = if (isSelected) IaVnzlaPurple else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) IaVnzlaPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = IaVnzlaPurpleLight
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (selectedTab) {
                        MarketTab.SHOP -> ShopTab(
                            state = state,
                            onSearchChange = viewModel::onSearchQueryChanged,
                            onCategorySelected = viewModel::onCategorySelected,
                            onFilterTypeSelected = viewModel::onFilterTypeSelected,
                            onSelectProduct = viewModel::selectProductForPurchase,
                            onDeleteProduct = { viewModel.deleteProduct(it) },
                            onOpenHistory = { viewModel.openAccountModal() },
                            onGoToPublish = { selectedTab = MarketTab.PUBLISH }
                        )
                        MarketTab.ORDERS -> OrdersTab(
                            state = state,
                            onSubmitBuyerPaymentRef = viewModel::submitBuyerPayment,
                            onSubmitSellerConfirmation = viewModel::submitSellerConfirmation,
                            onCancelOrder = viewModel::cancelOrder
                        )
                        MarketTab.PUBLISH -> PublishTab(
                            currentUser = state.currentUser,
                            onPublish = { name, category, desc, unit, retail, wholesale, minQty, stock, pagoMovil, binanceId, iaCuotasEligible ->
                                viewModel.addProduct(
                                    name, category, desc, unit, retail, wholesale, minQty, stock, pagoMovil, binanceId, iaCuotasEligible
                                )
                                selectedTab = MarketTab.SHOP
                            }
                        )
                        MarketTab.ACCOUNT -> AccountTab(
                            state = state,
                            onSwitchUser = viewModel::switchActiveUser,
                            onOpenSettings = { viewModel.openSettingsModal() },
                            onOpenHistory = { viewModel.openAccountModal() },
                            onLogout = { viewModel.logout() }
                        )
                    }

                    // Purchase Modal Popup
                    state.selectedProductForPurchase?.let { product ->
                        PurchaseModal(
                            product = product,
                            bcvRate = state.bcvRate,
                            usdtRate = state.usdtRate,
                            onDismiss = { viewModel.selectProductForPurchase(null) },
                            onConfirmOrder = { quantity, paymentMethod ->
                                viewModel.createOrder(product, quantity, paymentMethod)
                                selectedTab = MarketTab.ORDERS
                            }
                        )
                    }

                    // Top-right Account & Purchase History Modal
                    if (state.isAccountModalOpen) {
                        AccountAndHistoryModal(
                            state = state,
                            onDismiss = { viewModel.closeAccountModal() },
                            onSwitchUser = { viewModel.switchActiveUser(it) },
                            onOpenSettings = {
                                viewModel.closeAccountModal()
                                viewModel.openSettingsModal()
                            },
                            onLogout = { viewModel.logout() },
                            onSubmitBuyerPaymentRef = viewModel::submitBuyerPayment,
                            onSubmitSellerConfirmation = viewModel::submitSellerConfirmation,
                            onCancelOrder = viewModel::cancelOrder
                        )
                    }

                    // Settings Modal
                    if (state.isSettingsModalOpen) {
                        SettingsModal(
                            settings = state.settings,
                            liveRates = state.liveRates,
                            onDismiss = { viewModel.closeSettingsModal() },
                            onUpdateTheme = viewModel::updateThemeMode,
                            onUpdateCurrency = viewModel::updateCurrencyMode,
                            onUpdateBcvRate = viewModel::updateCustomBcvRate,
                            onRefreshRates = { viewModel.refreshExchangeRates() },
                            onToggleHighPerf = viewModel::toggleHighPerformanceMode,
                            onToggleIaCuotas = viewModel::toggleIaCuotasBadges,
                            onViewTerms = { viewModel.viewTermsScreen() },
                            onLogout = { viewModel.logout() },
                            onClearData = {
                                viewModel.clearAllUserData()
                                viewModel.closeSettingsModal()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopTab(
    state: com.veneca.app.ui.MarketplaceUiState,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFilterTypeSelected: (String) -> Unit,
    onSelectProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onOpenHistory: () -> Unit,
    onGoToPublish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Banner iavnzla
        item(key = "iavnzla_hero") {
            IaVnzlaHeroBanner(
                user = state.currentUser,
                onViewHistoryClicked = onOpenHistory
            )
        }

        // Barra de búsqueda con detalles y filtros
        item(key = "search_bar") {
            SearchWithDetailsBar(
                query = state.query,
                onQueryChange = onSearchChange,
                resultsCount = state.products.size,
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelected,
                filterType = state.filterType,
                onFilterTypeSelected = onFilterTypeSelected
            )
        }

        // Empty State: limpio por defecto para que los usuarios publiquen
        if (state.products.isEmpty()) {
            item(key = "empty_state") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(IaVnzlaPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AddBusiness,
                                contentDescription = null,
                                tint = IaVnzlaPurple,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.query.isBlank()) "Catálogo listo para publicar" else "Sin resultados para '${state.query}'",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay productos predefinidos. Todos los artículos son publicados por los usuarios y emprendedores de iavnzla con precios al detal y al mayor.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onGoToPublish,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publicar mi primer producto", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        } else {
            items(state.products, key = { it.id }) { product ->
                ModernProductCard(
                    product = product,
                    bcvRate = state.bcvRate,
                    usdtRate = state.usdtRate,
                    settings = state.settings,
                    onBuyClicked = { onSelectProduct(product) },
                    onDeleteClicked = if (product.sellerId == state.currentUser.id) onDeleteProduct else null
                )
            }
        }
    }
}

@Composable
private fun OrdersTab(
    state: com.veneca.app.ui.MarketplaceUiState,
    onSubmitBuyerPaymentRef: (orderId: String, refId: String) -> Unit,
    onSubmitSellerConfirmation: (orderId: String, verificationId: String) -> Unit,
    onCancelOrder: (orderId: String) -> Unit
) {
    var orderFilter by rememberSaveable { mutableStateOf("Todas") }

    val filteredOrders = remember(state.orders, orderFilter) {
        when (orderFilter) {
            "iaCuotas" -> state.orders.filter { it.isIaCuotasOrder }
            "Pendientes" -> state.orders.filter { it.status == OrderStatus.PENDING_PAYMENT }
            "Por Verificar" -> state.orders.filter { it.status == OrderStatus.PAID_PENDING_SELLER }
            "Completadas" -> state.orders.filter { it.status == OrderStatus.COMPLETED }
            else -> state.orders
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        // Banner informativo P2P & Cuotas
        item(key = "banner_p2p") {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IaVnzlaPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Pagos P2P Protegidos & iaCuotas", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Custodia (Escrow) y Verificación de ID de Pago", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Realiza el pago por Pago Móvil, Binance Pay o cuota inicial de iaCuotas.\n2. Notifica el comprobante para que el vendedor libere los fondos y despache el pedido.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Filter chips for orders
        item(key = "order_filters") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todas", "iaCuotas", "Pendientes", "Por Verificar", "Completadas").forEach { filter ->
                    val selected = orderFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { orderFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            item(key = "empty_orders") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No hay órdenes registradas", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Crea o publica una orden en el catálogo para comenzar transacciones.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(filteredOrders, key = { it.id }) { order ->
                P2POrderCard(
                    order = order,
                    currentUser = state.currentUser,
                    onSubmitBuyerPaymentRef = onSubmitBuyerPaymentRef,
                    onSubmitSellerConfirmation = onSubmitSellerConfirmation,
                    onCancelOrder = onCancelOrder
                )
            }
        }
    }
}

@Composable
private fun PublishTab(
    currentUser: com.veneca.app.model.User,
    onPublish: (
        name: String, category: String, desc: String, unit: String,
        retail: String, wholesale: String, minQty: String, stock: String,
        pagoMovil: String, binanceId: String, iaCuotasEligible: Boolean
    ) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Alimentos y Víveres") }
    var desc by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf("Bulto") }
    var retailPrice by rememberSaveable { mutableStateOf("") }
    var wholesalePrice by rememberSaveable { mutableStateOf("") }
    var minWholesaleQty by rememberSaveable { mutableStateOf("5") }
    var stock by rememberSaveable { mutableStateOf("50") }
    var pagoMovil by rememberSaveable { mutableStateOf(currentUser.pagoMovilData ?: "") }
    var binanceId by rememberSaveable { mutableStateOf(currentUser.binanceId ?: "") }
    var iaCuotasEligible by rememberSaveable { mutableStateOf(true) }

    val categories = listOf("Alimentos y Víveres", "Tecnología", "Ferretería y Electricidad", "Hogar y Limpieza", "Ropa y Calzado")
    val units = listOf("Bulto", "Caja", "Docena", "Paquete", "Unidad", "Kilo")

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedLabelColor = IaVnzlaPurple,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        focusedBorderColor = IaVnzlaPurple,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = IaVnzlaPurple
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Publicar Artículo en iavnzla", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Establece precio al detal, opción de iaCuotas y precio al mayor para compras en volumen.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Artículo") },
                placeholder = { Text("Ej: Harina PAN, Café, Bombillos, Cables...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = tfColors
            )
        }

        item {
            Text("Categoría:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val selected = category == cat
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { category = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Text("Presentación / Unidad:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(units) { u ->
                    val selected = unit == u
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { unit = u }
                    ) {
                        Text(
                            text = u,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Descripción comercial (detalles, origen, presentación)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = tfColors
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = retailPrice,
                    onValueChange = { retailPrice = it },
                    label = { Text("Precio Detal ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = tfColors
                )

                OutlinedTextField(
                    value = wholesalePrice,
                    onValueChange = { wholesalePrice = it },
                    label = { Text("Precio Mayor ($)") },
                    placeholder = { Text("Opcional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = tfColors
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = minWholesaleQty,
                    onValueChange = { minWholesaleQty = it },
                    label = { Text("Mínimo Mayorista") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = tfColors
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Disponible") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = tfColors
                )
            }
        }

        // iaCuotas Enable Switch
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Aceptar Pagos con iaCuotas (3 Cuotas)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Los clientes pagan 40% inicial y el resto en 3 cuotas quincenales al 0% de interés.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = iaCuotasEligible,
                        onCheckedChange = { iaCuotasEligible = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = IaVnzlaPurple)
                    )
                }
            }
        }

        item {
            Text("Datos para recibir pagos en Escrow:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = pagoMovil,
                onValueChange = { pagoMovil = it },
                label = { Text("Datos de Pago Móvil (Banco, CI, Teléfono)") },
                placeholder = { Text("Ej: Banco Venezuela 0102 · CI 12345678 · 0412-1234567") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = tfColors
            )
        }

        item {
            OutlinedTextField(
                value = binanceId,
                onValueChange = { binanceId = it },
                label = { Text("Binance Pay ID / Paymail") },
                placeholder = { Text("Ej: 123456789 o correo@binance") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = tfColors
            )
        }

        item {
            Button(
                onClick = {
                    onPublish(name, category, desc, unit, retailPrice, wholesalePrice, minWholesaleQty, stock, pagoMovil, binanceId, iaCuotasEligible)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar Artículo en Catálogo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun AccountTab(
    state: com.veneca.app.ui.MarketplaceUiState,
    onSwitchUser: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(IaVnzlaPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.currentUser.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = IaVnzlaPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = state.currentUser.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${state.currentUser.role.label} · ${state.currentUser.cedula}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = IaVnzlaPurpleLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Nivel ${state.currentUser.iaVnzlaLevel} iavnzla",
                                color = IaVnzlaPurpleDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Línea iaCrédito
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Línea iaCrédito Disponible:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${"%.2f".format(state.currentUser.iaCreditAvailable)} disponible", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VenecaGreen)
                            }
                            Button(
                                onClick = onOpenHistory,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Ver Compras", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }

                    // Quick Settings
                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Configuración y Ajustes de iavnzla")
                    }
                }
            }
        }

        // Fast Account Switcher: ONLY if there are multiple accounts registered by the user
        if (state.allUsers.size > 1) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cuentas registradas en este dispositivo:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        state.allUsers.forEach { user ->
                            val isCurrent = user.id == state.currentUser.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) IaVnzlaPurpleLight else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) IaVnzlaPurple else MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSwitchUser(user.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isCurrent) IaVnzlaPurpleDark else MaterialTheme.colorScheme.onSurface)
                                        Text("${user.role.label} · ${user.cedula}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isCurrent) {
                                        Surface(color = IaVnzlaPurple, shape = RoundedCornerShape(4.dp)) {
                                            Text("ACTIVO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Logout Button
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cerrar Sesión en iavnzla", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}
