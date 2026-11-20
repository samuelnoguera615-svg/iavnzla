package com.veneca.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veneca.app.model.User
import com.veneca.app.model.UserRole
import com.veneca.app.ui.theme.*

@Composable
fun AuthScreen(
    allUsers: List<User> = emptyList(),
    onLogin: (identifier: String) -> Unit,
    onRegister: (name: String, email: String, cedula: String, phone: String, role: UserRole, business: String?, pagoMovil: String?, binanceId: String?) -> Unit,
    onQuickSwitchUser: (String) -> Unit = {}
) {
    var isRegisterMode by rememberSaveable { mutableStateOf(false) }

    // Login Form State
    var loginIdentifier by rememberSaveable { mutableStateOf("") }

    // Register Form State
    var regName by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("") }
    var regCedula by rememberSaveable { mutableStateOf("") }
    var regPhone by rememberSaveable { mutableStateOf("") }
    var regRole by rememberSaveable { mutableStateOf(UserRole.BUYER) }
    var regBusiness by rememberSaveable { mutableStateOf("") }
    var regPagoMovil by rememberSaveable { mutableStateOf("") }
    var regBinance by rememberSaveable { mutableStateOf("") }

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Brand Logo & Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(IaVnzlaPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ia",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "iavnzla",
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mercado P2P · Compras Mayoristas · iaCuotas 0%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tab Selector: Iniciar Sesión / Crear Cuenta
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isRegisterMode = false },
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isRegisterMode) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!isRegisterMode) IaVnzlaPurple else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            textAlign = TextAlign.Center,
                            color = if (!isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isRegisterMode = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isRegisterMode) IaVnzlaPurple else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isRegisterMode) IaVnzlaPurple else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = "Crear Cuenta",
                            textAlign = TextAlign.Center,
                            color = if (isRegisterMode) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }

            // Form Content
            if (!isRegisterMode) {
                // LOGIN VIEW
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Acceder a tu cuenta en iavnzla",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = { loginIdentifier = it },
                                label = { Text("Correo o Cédula / RIF") },
                                placeholder = { Text("Ej: carlos@ejemplo.com o V-26491823") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = tfColors
                            )

                            Button(
                                onClick = { onLogin(loginIdentifier) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                            ) {
                                Text("Entrar a iavnzla", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Text(
                                text = "¿No tienes cuenta? Toca en 'Crear Cuenta' arriba para registrarte.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                // REGISTER VIEW
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Crear nueva cuenta en iavnzla",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it },
                                label = { Text("Nombre y Apellido") },
                                placeholder = { Text("Ej: María Pérez") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = tfColors
                            )

                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text("Correo Electrónico") },
                                placeholder = { Text("ejemplo@correo.com") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = tfColors
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = regCedula,
                                    onValueChange = { regCedula = it },
                                    label = { Text("Cédula / RIF") },
                                    placeholder = { Text("V-12.345.678") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = tfColors
                                )

                                OutlinedTextField(
                                    value = regPhone,
                                    onValueChange = { regPhone = it },
                                    label = { Text("WhatsApp / Telf") },
                                    placeholder = { Text("0412-1234567") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = tfColors
                                )
                            }

                            // Role Selection
                            Text(
                                "Tipo de Usuario:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(UserRole.BUYER, UserRole.SELLER_WHOLESALE).forEach { r ->
                                    val selected = regRole == r
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { regRole = r },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.surfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (selected) IaVnzlaPurple else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            text = r.label,
                                            textAlign = TextAlign.Center,
                                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                            }

                            if (regRole == UserRole.SELLER_WHOLESALE) {
                                OutlinedTextField(
                                    value = regBusiness,
                                    onValueChange = { regBusiness = it },
                                    label = { Text("Nombre de la Empresa o Negocio") },
                                    placeholder = { Text("Ej: Distribuidora Caracas C.A.") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = tfColors
                                )
                                OutlinedTextField(
                                    value = regPagoMovil,
                                    onValueChange = { regPagoMovil = it },
                                    label = { Text("Datos de Pago Móvil para Cobrar") },
                                    placeholder = { Text("Banco 0102, 04121234567, V-12345678") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = tfColors
                                )
                                OutlinedTextField(
                                    value = regBinance,
                                    onValueChange = { regBinance = it },
                                    label = { Text("Binance Pay ID / Paymail") },
                                    placeholder = { Text("Ej: 123456789 o pay@iavnzla") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = tfColors
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    onRegister(regName, regEmail, regCedula, regPhone, regRole, regBusiness, regPagoMovil, regBinance)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IaVnzlaPurple)
                            ) {
                                Text("Completar Registro", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
