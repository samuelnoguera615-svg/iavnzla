package com.veneca.app.ui.components

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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veneca.app.model.*
import com.veneca.app.ui.theme.*

@Composable
fun VenecaTopHeader(
    currentUser: User,
    allUsers: List<User>,
    liveRates: LiveExchangeRates,
    onOpenAccount: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefreshRates: () -> Unit
) {
    Surface(
        color = VenecaSlateDark,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Main App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Subtitle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IaVnzlaPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ia",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "iavnzla",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = IaVnzlaPurpleLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "P2P + MAYOR + CUOTAS",
                                    color = IaVnzlaPurpleDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Binance P2P · Alibaba · iaCuotas 0%",
                            color = VenecaMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Top Right Action Buttons (Settings & Account / Purchase History)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Settings Button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Account & Purchase History Button
                    Surface(
                        color = IaVnzlaPurple,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { onOpenAccount() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Cuenta e Historial",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Text(
                                    text = currentUser.name.split(" ").firstOrNull() ?: currentUser.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Nivel ${currentUser.iaVnzlaLevel} · Compras",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 9.sp,
                                    lineHeight = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LIVE API TICKER: BCV Oficial & USDT / Paralelo en Bs.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // BCV Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BCV:",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Bs. ${"%.2f".format(liveRates.bcvRate)}",
                            color = VenecaAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text("·", color = Color.Gray, fontSize = 12.sp)

                    // USDT Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "USDT:",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Bs. ${"%.2f".format(liveRates.usdtRate)}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Refresh Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRefreshRates() }
                ) {
                    if (liveRates.isLoading) {
                        CircularProgressIndicator(
                            color = VenecaAmber,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar Tasas",
                            tint = Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = liveRates.lastUpdated,
                        color = Color.LightGray,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun IaVnzlaHeroBanner(
    user: User,
    onViewHistoryClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF312E81)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(IaVnzlaBannerGradient)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "LÍNEA IACRÉDITO",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nivel ${user.iaVnzlaLevel} iavnzla",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.clickable { onViewHistoryClicked() }
                    ) {
                        Text(
                            text = "Mis Compras",
                            color = IaVnzlaPurpleDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Línea de compra disponible:",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${"%.2f".format(user.iaCreditAvailable)}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Límite: $${"%.2f".format(user.iaCreditLimit)}",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val progress = if (user.iaCreditLimit > 0) {
                    (user.iaCreditAvailable / user.iaCreditLimit).toFloat().coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = VenecaGreen,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "⚡ Paga el 40% inicial y el resto en 3 cuotas quincenales sin interés (iaCuotas).",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun SearchWithDetailsBar(
    query: String,
    onQueryChange: (String) -> Unit,
    resultsCount: Int,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    filterType: String,
    onFilterTypeSelected: (String) -> Unit
) {
    val categories = remember {
        listOf("Todos", "Alimentos y Víveres", "Tecnología", "Ferretería y Electricidad", "Ropa y Calzado")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar artículos en iavnzla...", color = VenecaMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = IaVnzlaPurple)
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = VenecaMuted)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = IaVnzlaPurple,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Filter chips: iaCuotas, Mayoristas, Al Detal, Todos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Todos", "iaCuotas", "Mayor", "Detal").forEach { type ->
                val selected = filterType == type
                val label = when (type) {
                    "iaCuotas" -> "⚡ iaCuotas (3 Cuotas)"
                    "Mayor" -> "📦 Mayoristas"
                    "Detal" -> "🛍️ Al Detal"
                    else -> "Todos"
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFilterTypeSelected(type) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = label,
                        textAlign = TextAlign.Center,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                    )
                }
            }
        }

        // Horizontal Category Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories, key = { it }) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) IaVnzlaPurple else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable { onCategorySelected(cat) }
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernProductCard(
    product: Product,
    bcvRate: Double,
    usdtRate: Double,
    settings: UserSettings,
    onBuyClicked: (Product) -> Unit,
    onDeleteClicked: ((String) -> Unit)? = null
) {
    val retailVesBcv = remember(product.price, bcvRate) { product.price * bcvRate }
    val retailVesUsdt = remember(product.price, usdtRate) { product.price * usdtRate }

    val downPaymentUsd = remember(product.price) { product.calculateDownPayment(product.price) }
    val downPaymentVes = remember(downPaymentUsd, bcvRate) { downPaymentUsd * bcvRate }
    val installmentUsd = remember(product.price) { product.calculateInstallmentAmount(product.price) }

    val elevation = if (settings.highPerformanceMode) 0.dp else 2.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Category & Unit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = IaVnzlaPurpleLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.category.uppercase(),
                            color = IaVnzlaPurpleDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.unit,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (onDeleteClicked != null) {
                    IconButton(
                        onClick = { onDeleteClicked(product.id) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar artículo",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Description
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Boxes: Detal vs Mayor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Detal Box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Precio al Detal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${"%.2f".format(product.price)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "BCV: Bs. ${"%.2f".format(retailVesBcv)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                // Wholesale Box
                if (product.wholesalePrice != null) {
                    val wholesaleVes = remember(product.wholesalePrice, bcvRate) { product.wholesalePrice * bcvRate }
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VenecaAmberLight)
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Mayorista (Min ${product.wholesaleMinimum} u.)",
                            color = Color(0xFF92400E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$${"%.2f".format(product.wholesalePrice)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "BCV: Bs. ${"%.2f".format(wholesaleVes)}",
                            color = Color(0xFF92400E),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // iaCuotas Banner (Financiamiento 3 Cuotas sin interés)
            if (product.iaCuotasEligible && settings.showIaCuotasBadges) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = IaVnzlaPurpleLight,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IaVnzlaPurpleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(IaVnzlaPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ia",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "iaCuotas: Inicial $${"%.2f".format(downPaymentUsd)} (~Bs. ${"%.2f".format(downPaymentVes)})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = IaVnzlaPurpleDark
                                )
                                Text(
                                    text = "+ 3 cuotas quincenales de $${"%.2f".format(installmentUsd)} (0% interés)",
                                    fontSize = 10.sp,
                                    color = IaVnzlaPurpleDark.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // 3 Installment Dots indicator
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(IaVnzlaPurple))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(IaVnzlaPurple.copy(alpha = 0.5f)))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(IaVnzlaPurple.copy(alpha = 0.5f)))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seller & Stock info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.sellerName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VenecaSlate
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verificado",
                        tint = VenecaGreen,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Text(
                    text = "Stock: ${product.stock} ${product.unit.lowercase()}s",
                    fontSize = 11.sp,
                    color = VenecaMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Buy Button
            Button(
                onClick = { onBuyClicked(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IaVnzlaPurple
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Comprar con iaCuotas o P2P",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PurchaseModal(
    product: Product,
    bcvRate: Double,
    usdtRate: Double,
    onDismiss: () -> Unit,
    onConfirmOrder: (quantity: Int, paymentMethod: String) -> Unit
) {
    var quantityText by rememberSaveable { mutableStateOf("1") }
    val quantity = quantityText.toIntOrNull() ?: 1
    var selectedPaymentMethod by rememberSaveable { mutableStateOf("iaCuotas (3 Cuotas 0% Int)") }

    val isWholesale = product.wholesalePrice != null && quantity >= product.wholesaleMinimum
    val activePriceUsd = if (isWholesale) product.wholesalePrice!! else product.price
    val totalUsd = activePriceUsd * quantity
    val totalVesBcv = totalUsd * bcvRate
    val totalVesUsdt = totalUsd * usdtRate

    val downPaymentUsd = product.calculateDownPayment(totalUsd)
    val downPaymentVes = downPaymentUsd * bcvRate
    val installmentUsd = product.calculateInstallmentAmount(totalUsd)
    val installmentVes = installmentUsd * bcvRate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(IaVnzlaPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Orden de Compra iavnzla",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = product.name,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quantity Stepper
                Text(
                    text = "Cantidad (${product.unit}):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            if (current > 1) quantityText = (current - 1).toString()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) quantityText = input
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = IaVnzlaPurple,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 1
                            quantityText = (current + 1).toString()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Payment Method Selector
                Text(
                    text = "Método de Pago:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val methods = listOf("iaCuotas (3 Cuotas 0% Int)", "Pago Móvil", "Binance Pay")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { method ->
                        val selected = selectedPaymentMethod == method
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) IaVnzlaPurpleLight else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = IaVnzlaPurple)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) IaVnzlaPurpleDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // If iaCuotas selected: Show financing breakdown
                if (selectedPaymentMethod.contains("iaCuotas")) {
                    Surface(
                        color = IaVnzlaPurpleLight,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IaVnzlaPurpleBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Plan iaCuotas iavnzla (0% Interés):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IaVnzlaPurpleDark)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pago Inicial hoy (40%):", fontSize = 11.sp, color = IaVnzlaPurpleDark)
                                Text("$${"%.2f".format(downPaymentUsd)} (Bs. ${"%.2f".format(downPaymentVes)})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IaVnzlaPurpleDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("3 Cuotas quincenales:", fontSize = 11.sp, color = IaVnzlaPurpleDark)
                                Text("3 × $${"%.2f".format(installmentUsd)} (Bs. ${"%.2f".format(installmentVes)})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IaVnzlaPurpleDark)
                            }
                        }
                    }
                }

                // Total Card with Dual BCV & USDT Conversion
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Artículo:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Text("$${"%.2f".format(totalUsd)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("A tasa BCV oficial:", color = VenecaMuted, fontSize = 11.sp)
                            Text("Bs. ${"%.2f".format(totalVesBcv)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VenecaGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("A tasa USDT / Binance:", color = VenecaMuted, fontSize = 11.sp)
                            Text("Bs. ${"%.2f".format(totalVesUsdt)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0284C7))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmOrder(quantity.coerceAtLeast(1), selectedPaymentMethod)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
            ) {
                Text("Confirmar Compra", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AccountAndHistoryModal(
    state: com.veneca.app.ui.MarketplaceUiState,
    onDismiss: () -> Unit,
    onSwitchUser: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    onSubmitBuyerPaymentRef: (String, String) -> Unit,
    onSubmitSellerConfirmation: (String, String) -> Unit,
    onCancelOrder: (String) -> Unit
) {
    var selectedModalTab by rememberSaveable { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IaVnzlaPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mi Cuenta e Historial",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = selectedModalTab,
                    containerColor = Color.Transparent,
                    contentColor = IaVnzlaPurple,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedModalTab == 0,
                        onClick = { selectedModalTab = 0 },
                        text = {
                            Text(
                                "Historial (${state.orders.size})",
                                fontWeight = if (selectedModalTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedModalTab == 1,
                        onClick = { selectedModalTab = 1 },
                        text = {
                            Text(
                                "Mi Perfil & iaCrédito",
                                fontWeight = if (selectedModalTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 460.dp)
            ) {
                if (selectedModalTab == 0) {
                    if (state.orders.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Aún no tienes compras registradas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Cuando compres artículos al detal o con iaCuotas, aparecerán aquí para su seguimiento.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(state.orders, key = { it.id }) { order ->
                                HistoryOrderItemCard(
                                    order = order,
                                    currentUser = state.currentUser,
                                    onSubmitBuyerPaymentRef = onSubmitBuyerPaymentRef,
                                    onSubmitSellerConfirmation = onSubmitSellerConfirmation,
                                    onCancelOrder = onCancelOrder
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VenecaBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(IaVnzlaPurpleLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = state.currentUser.name.take(2).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = IaVnzlaPurple
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(state.currentUser.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${state.currentUser.cedula} · ${state.currentUser.email}", fontSize = 11.sp, color = VenecaMuted)
                                        }
                                    }

                                    HorizontalDivider(color = VenecaBorder)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Nivel iavnzla:", fontSize = 11.sp, color = VenecaMuted)
                                        Text("Nivel ${state.currentUser.iaVnzlaLevel} (Excelente)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IaVnzlaPurpleDark)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Línea iaCrédito Disponible:", fontSize = 11.sp, color = VenecaMuted)
                                        Text("$${"%.2f".format(state.currentUser.iaCreditAvailable)}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = VenecaGreen)
                                    }
                                }
                            }
                        }

                        // Switch Accounts (only if multiple accounts exist)
                        if (state.allUsers.size > 1) {
                            item {
                                Text("Cuentas en este dispositivo:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    state.allUsers.forEach { u ->
                                        val isCurrent = u.id == state.currentUser.id
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isCurrent) IaVnzlaPurpleLight else MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) IaVnzlaPurple else MaterialTheme.colorScheme.outline),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onSwitchUser(u.id) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(u.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                                    Text("${u.role.label} · ${u.cedula}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                        // Settings and Logout
                        item {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onOpenSettings()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajustes de Configuración", fontSize = 12.sp)
                            }
                        }

                        item {
                            TextButton(
                                onClick = onLogout,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cerrar Sesión", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
            ) {
                Text("Listo")
            }
        }
    )
}

@Composable
fun HistoryOrderItemCard(
    order: P2POrder,
    currentUser: User,
    onSubmitBuyerPaymentRef: (String, String) -> Unit,
    onSubmitSellerConfirmation: (String, String) -> Unit,
    onCancelOrder: (String) -> Unit
) {
    var refInput by rememberSaveable(order.id) { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.id, fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (order.isIaCuotasOrder) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(color = IaVnzlaPurpleLight, shape = RoundedCornerShape(4.dp)) {
                            Text("IACUOTAS 0%", color = IaVnzlaPurpleDark, fontWeight = FontWeight.Bold, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }
                Surface(
                    color = Color(order.status.badgeColorHex).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        order.status.label,
                        color = Color(order.status.badgeColorHex),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(order.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${order.quantity} unid. · ${order.paymentMethod}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${"%.2f".format(order.totalUsd)} (~Bs. ${"%.2f".format(order.totalVes)})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VenecaGreen)
            }

            // Installment Breakdown if iaCuotas
            if (order.isIaCuotasOrder) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IaVnzlaPurpleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Estado de Cuotas iaCuotas:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = IaVnzlaPurpleDark)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Inicial ($${"%.2f".format(order.iaDownPaymentUsd)}):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(if (order.iaInstallmentsPaid >= 1) "Pagada ✓" else "Pendiente", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (order.iaInstallmentsPaid >= 1) VenecaGreen else VenecaAmber)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Cuota 1 ($${"%.2f".format(order.iaInstallmentAmountUsd)}):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(if (order.iaInstallmentsPaid >= 2) "Pagada ✓" else "En 14 días", fontSize = 10.sp, color = if (order.iaInstallmentsPaid >= 2) VenecaGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("• Cuota 2 ($${"%.2f".format(order.iaInstallmentAmountUsd)}):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(if (order.iaInstallmentsPaid >= 3) "Pagada ✓" else "En 28 días", fontSize = 10.sp, color = if (order.iaInstallmentsPaid >= 3) VenecaGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (order.status == OrderStatus.PENDING_PAYMENT) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = refInput,
                        onValueChange = { refInput = it },
                        placeholder = { Text("ID de comprobante", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = { onSubmitBuyerPaymentRef(order.id, refInput) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                    ) {
                        Text("Notificar", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsModal(
    settings: UserSettings,
    liveRates: LiveExchangeRates,
    onDismiss: () -> Unit,
    onUpdateTheme: (AppThemeMode) -> Unit,
    onUpdateCurrency: (CurrencyDisplayMode) -> Unit,
    onUpdateBcvRate: (Double) -> Unit,
    onRefreshRates: () -> Unit,
    onToggleHighPerf: (Boolean) -> Unit,
    onToggleIaCuotas: (Boolean) -> Unit,
    onViewTerms: () -> Unit,
    onLogout: () -> Unit,
    onClearData: () -> Unit
) {
    var bcvInput by rememberSaveable { mutableStateOf(settings.customBcvRate.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(IaVnzlaPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Configuración iavnzla", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TEMA VISUAL
                item {
                    Text("Tema Visual:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppThemeMode.entries.forEach { mode ->
                            val selected = settings.themeMode == mode
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateTheme(mode) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = when (mode) {
                                        AppThemeMode.LIGHT -> "Claro"
                                        AppThemeMode.DARK -> "Oscuro"
                                        AppThemeMode.SYSTEM -> "Sistema"
                                    },
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // API DE TASAS EN TIEMPO REAL
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tasas en Vivo (API ve.dolarapi):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                TextButton(onClick = onRefreshRates, contentPadding = PaddingValues(0.dp)) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Actualizar", fontSize = 11.sp)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• Dólar Oficial BCV:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Bs. ${"%.2f".format(liveRates.bcvRate)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VenecaAmber)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("• USDT / Binance P2P:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Bs. ${"%.2f".format(liveRates.usdtRate)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0284C7))
                            }
                        }
                    }
                }

                // TASA PERSONALIZADA
                item {
                    Text("Tasa BCV Manual Personalizada:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = bcvInput,
                            onValueChange = { bcvInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = IaVnzlaPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Button(
                            onClick = {
                                val parsed = bcvInput.toDoubleOrNull()
                                if (parsed != null && parsed > 0) onUpdateBcvRate(parsed)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                        ) {
                            Text("Fijar", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                // MODO ALTO RENDIMIENTO (Eliminación de Lag)
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Modo Alto Rendimiento (Sin Lag)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Optimiza el renderizado de la GPU para fluidez máxima a 120 FPS.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.highPerformanceMode,
                                onCheckedChange = onToggleHighPerf,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = VenecaGreen)
                            )
                        }
                    }
                }

                // MOSTRAR INSIGNIAS IACUOTAS
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Insignias de iaCuotas en Catálogo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Muestra el plan de 3 cuotas quincenales a 0% de interés.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.showIaCuotasBadges,
                                onCheckedChange = onToggleIaCuotas,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = IaVnzlaPurple)
                            )
                        }
                    }
                }

                // VER TERMINOS
                item {
                    OutlinedButton(
                        onClick = onViewTerms,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Términos y Condiciones de iavnzla", fontSize = 11.sp)
                    }
                }

                // CERRAR SESION
                item {
                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cerrar Sesión", color = Color.Red, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
            ) {
                Text("Listo")
            }
        }
    )
}

@Composable
fun P2POrderCard(
    order: P2POrder,
    currentUser: User,
    onSubmitBuyerPaymentRef: (orderId: String, refId: String) -> Unit,
    onSubmitSellerConfirmation: (orderId: String, verificationId: String) -> Unit,
    onCancelOrder: (orderId: String) -> Unit
) {
    var buyerInputRef by rememberSaveable(order.id) { mutableStateOf("") }
    var sellerInputRef by rememberSaveable(order.id) { mutableStateOf(order.buyerPaymentRef ?: "") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: ID and Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Orden #${order.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (order.isIaCuotasOrder) {
                        Surface(
                            color = IaVnzlaPurpleLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "IACUOTAS",
                                color = IaVnzlaPurpleDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    } else if (order.isWholesale) {
                        Surface(
                            color = VenecaAmberLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "MAYORISTA",
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = Color(order.status.badgeColorHex).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.status.label,
                        color = Color(order.status.badgeColorHex),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Order items breakdown
            Column {
                Text(
                    text = order.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${order.quantity} unidades × $${"%.2f".format(order.unitPriceUsd)}",
                        fontSize = 11.sp,
                        color = VenecaMuted
                    )
                    Text(
                        text = "$${"%.2f".format(order.totalUsd)} (~Bs. ${"%.2f".format(order.totalVes)})",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = VenecaGreen
                    )
                }
            }

            // Participant info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Comprador", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(order.buyerName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Vendedor", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(order.sellerName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Payment method & instructions
            Surface(
                color = if (order.isIaCuotasOrder) IaVnzlaPurpleLight else Color(0xFFEFF6FF),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (order.isIaCuotasOrder) IaVnzlaPurpleBorder else Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = if (order.isIaCuotasOrder) IaVnzlaPurpleDark else Color(0xFF1D4ED8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pago con ${order.paymentMethod}:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (order.isIaCuotasOrder) IaVnzlaPurpleDark else Color(0xFF1E40AF)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.sellerPaymentDetails,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Actions for PENDING_PAYMENT
            if (order.status == OrderStatus.PENDING_PAYMENT) {
                Surface(
                    color = VenecaAmberLight,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Paso 1: Realiza el pago e ingresa la ID de pago / comprobante",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                        OutlinedTextField(
                            value = buyerInputRef,
                            onValueChange = { buyerInputRef = it },
                            placeholder = { Text("Ej: PAGOMOVIL-849201 o TxID Binance", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = IaVnzlaPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onCancelOrder(order.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancelar", color = Color.Red, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSubmitBuyerPaymentRef(order.id, buyerInputRef) },
                                modifier = Modifier.weight(2f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                            ) {
                                Text("Notificar Pago", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Actions for PAID_PENDING_SELLER
            if (order.status == OrderStatus.PAID_PENDING_SELLER) {
                Surface(
                    color = IaVnzlaPurpleLight,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IaVnzlaPurpleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Paso 2: Verificación del Vendedor", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IaVnzlaPurpleDark)
                        Text("ID Comprador: ${order.buyerPaymentRef ?: "Sin ID"}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = IaVnzlaPurpleDark)
                        OutlinedTextField(
                            value = sellerInputRef,
                            onValueChange = { sellerInputRef = it },
                            placeholder = { Text("ID de verificación bancaria", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = IaVnzlaPurple,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Button(
                            onClick = { onSubmitSellerConfirmation(order.id, sellerInputRef) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VenecaGreen)
                        ) {
                            Text("Confirmar y Liberar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            // Status COMPLETED
            if (order.status == OrderStatus.COMPLETED) {
                Surface(
                    color = VenecaGreenLight,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VenecaGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Orden completada con éxito.", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VenecaGreenDark)
                    }
                }
            }
        }
    }
}
