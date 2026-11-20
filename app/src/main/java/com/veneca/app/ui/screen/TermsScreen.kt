package com.veneca.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veneca.app.ui.theme.*

@Composable
fun TermsScreen(
    onAcceptTerms: () -> Unit
) {
    var acceptedCheckbox by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Brand iavnzla & Icon
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "iavnzla",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )

            Text(
                text = "Términos, Condiciones y Política de Privacidad",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = IaVnzlaPurple,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Terms Document
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Bienvenido a iavnzla",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "iavnzla es una plataforma de comercio y marketplace diseñada para unir el comercio al mayor y detal, la protección de pagos P2P con custodia (escrow) y el financiamiento accesible en cuotas sin interés en Venezuela.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }

                    item {
                        Text(
                            text = "1. Reglas de Publicación de Productos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "El catálogo es completamente transparente y autogestionado por los usuarios y emprendedores. Cada vendedor es responsable del inventario físico, especificaciones y precios al detal o al mayor que publique. Queda estrictamente prohibida la publicación de artículos ilícitos.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }

                    item {
                        Text(
                            text = "2. Protección de Pagos P2P (Escrow)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Las transacciones directas entre comprador y vendedor se rigen por un sistema de verificación cruzada de ID/referencia bancaria (Pago Móvil o Binance Pay). Ninguna orden se considerará completada hasta que ambas partes hayan confirmado el comprobante.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }

                    item {
                        Text(
                            text = "3. Financiamiento en Cuotas (iaCuotas)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "El servicio de iaCuotas permite a compradores verificados adquirir productos pagando una inicial del 40% y el saldo restante en 3 cuotas quincenales al 0% de interés. El cumplimiento puntual de las cuotas incrementa su línea de iaCrédito y nivel en la plataforma.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }

                    item {
                        Text(
                            text = "4. Política de Privacidad y Datos Personales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Su información personal (Cédula de Identidad/RIF, teléfono y datos de cobro) se almacena bajo estrictos estándares de confidencialidad y solo se comparte entre los participantes de una orden para concretar la entrega y la verificación del pago.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }

                    item {
                        Text(
                            text = "5. Moneda y Tasas de Referencia",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "La aplicación proporciona tasas referenciales en tiempo real obtenidas mediante APIs públicas oficiales del Banco Central de Venezuela (BCV) y mercado USDT para facilitar la equivalencia precisa en Bolívares (Bs.).",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checkbox Agreement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptedCheckbox,
                    onCheckedChange = { acceptedCheckbox = it },
                    colors = CheckboxDefaults.colors(checkedColor = IaVnzlaPurple)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "He leído y acepto los Términos, Condiciones y Política de Privacidad de iavnzla.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Accept and Proceed Button
            Button(
                onClick = onAcceptTerms,
                enabled = acceptedCheckbox,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IaVnzlaPurple,
                    disabledContainerColor = IaVnzlaPurple.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = "Aceptar y Continuar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
