package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DepositRequestEntity
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun PaymentGatewayModal(
    depositSession: DepositRequestEntity,
    isProcessing: Boolean,
    onConfirmPayment: (simulatedFail: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var otpCode by remember { mutableStateOf("942810") }
    var show3DSecureStep by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
            border = BorderStroke(1.dp, VeyraNavyBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("payment_gateway_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Provider Branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VeyraEmerald.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = VeyraEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Təhlükəsiz Ödəniş Şlüzü",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VeyraTextPrimary
                            )
                            Text(
                                text = "256-bit SSL & 3D Secure 2.0",
                                fontSize = 10.sp,
                                color = VeyraEmerald
                            )
                        }
                    }

                    Surface(
                        color = VeyraNavyElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "SANDBOX",
                            color = VeyraGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = VeyraNavyBorder)
                Spacer(modifier = Modifier.height(16.dp))

                if (!show3DSecureStep) {
                    // Order Summary
                    Text(
                        text = "Ödəniləcək məbləğ",
                        fontSize = 12.sp,
                        color = VeyraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.2f AZN", depositSession.amountCents / 100.0),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gateway Details Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sifariş Nömrəsi:", fontSize = 12.sp, color = VeyraTextMuted)
                            Text(depositSession.depositIdCode, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VeyraTextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ödəniş Metodu:", fontSize = 12.sp, color = VeyraTextMuted)
                            Text(depositSession.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VeyraTextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Token Sessiyası:", fontSize = 12.sp, color = VeyraTextMuted)
                            Text("${depositSession.gatewaySessionToken.take(12)}...", fontSize = 12.sp, color = VeyraTextSecondary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("İmza Təsdiqi:", fontSize = 12.sp, color = VeyraTextMuted)
                            Text("SHA-256 (Aktiv)", fontSize = 12.sp, color = VeyraEmerald, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { show3DSecureStep = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("gateway_proceed_button")
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3D Secure ilə Təsdiq et", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    // 3D Secure SMS Verification Step
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = VeyraEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bank 3D Secure Təsdiqi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = VeyraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bankınız tərəfindən göndərilmiş 6 rəqəmli OTP kodunu daxil edin:",
                        fontSize = 12.sp,
                        color = VeyraTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("Birdəfəlik OTP Kodu", color = VeyraTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gateway_otp_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isProcessing) {
                        CircularProgressIndicator(color = VeyraEmerald, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Server və Webhook imzası yoxlanılır...",
                            fontSize = 12.sp,
                            color = VeyraEmerald
                        )
                    } else {
                        Button(
                            onClick = { onConfirmPayment(false) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("gateway_confirm_button")
                        ) {
                            Text("Ödənişi Tamamla", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { onConfirmPayment(true) },
                            modifier = Modifier.testTag("gateway_simulate_fail_button")
                        ) {
                            Text("Test: Uğursuz Ödəniş Simulyasiyası", fontSize = 12.sp, color = VeyraError)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    enabled = !isProcessing
                ) {
                    Text("İmtina et", color = VeyraTextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}
