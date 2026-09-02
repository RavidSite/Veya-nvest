package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun WithdrawalScreen(
    user: UserEntity?,
    onRequestWithdrawal: (amountAz: Double, bankName: String, iban: String, cardLast4: String, recipientName: String) -> Unit,
    onKycRedirect: () -> Unit,
    errorMessage: String?
) {
    val availableBalanceAz = (user?.balanceCents ?: 0L) / 100.0

    var amountText by remember { mutableStateOf("50") }
    var bankName by remember { mutableStateOf("Kapital Bank ASC") }
    var ibanText by remember { mutableStateOf("AZ21NABZ01350100000000123456") }
    var cardLast4Text by remember { mutableStateOf("4821") }
    var recipientNameText by remember { mutableStateOf(user?.fullName ?: "Rəşad Əliyev") }

    val amountEntered = amountText.toDoubleOrNull() ?: 0.0
    val calculatedCommission = if (amountEntered > 0) maxOf(0.50, amountEntered * 0.005) else 0.0
    val netPayout = if (amountEntered > calculatedCommission) amountEntered - calculatedCommission else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("withdrawal_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Vəsaiti Çıxar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                Text(
                    text = "Azərbaycan bank hesabınıza və ya kartınıza rəsmi köçürmə",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary
                    )
                )
            }
        }

        // KYC Warning if not verified
        if (user?.kycStatus != "Təsdiqləndi") {
            item {
                Surface(
                    color = VeyraGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, VeyraGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = VeyraGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Şəxsiyyət Təsdiqi Tələb Olunur (KYC)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VeyraGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Maliyyə təhlükəsizliyi qaydalarına əsasən, çıxarış etmək üçün şəxsiyyət vəsiqəsi məlumatlarınız təsdiqlənməlidir.",
                            fontSize = 11.sp,
                            color = VeyraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onKycRedirect,
                            colors = ButtonDefaults.buttonColors(containerColor = VeyraGold, contentColor = Color(0xFF281800)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("withdraw_kyc_button")
                        ) {
                            Text("Şəxsiyyəti Təsdiq Et", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            item {
                Surface(
                    color = VeyraError.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = VeyraError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Available Balance Info Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Çıxarıla Bilən Sərbəst Balans", fontSize = 12.sp, color = VeyraTextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f AZN", availableBalanceAz),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraEmerald
                            )
                        )
                    }

                    TextButton(onClick = { amountText = String.format(Locale.US, "%.2f", availableBalanceAz) }) {
                        Text("Maksimum", color = VeyraEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Withdrawal Form
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Bank və Hesab Məlumatları", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VeyraTextPrimary)

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Çıxarış Məbləği (AZN)", color = VeyraTextMuted) },
                        trailingIcon = { Text("AZN", color = VeyraEmerald, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
                    )

                    OutlinedTextField(
                        value = recipientNameText,
                        onValueChange = { recipientNameText = it },
                        label = { Text("Alan Şəxsin Ad və Soyadı", color = VeyraTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bankın Adı (məs: ABB, Kapital Bank)", color = VeyraTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ibanText,
                        onValueChange = { ibanText = it },
                        label = { Text("Bank İBAN Hesabı (AZ...)", color = VeyraTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardLast4Text,
                        onValueChange = { cardLast4Text = it },
                        label = { Text("Kartın Son 4 Rəqəmi (İstəyə bağlı)", color = VeyraTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Financial Calculation Breakdown
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Çıxarışın İcmalı", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VeyraTextPrimary)
                    HorizontalDivider(color = VeyraNavyBorder)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Çıxarış Məbləği:", fontSize = 12.sp, color = VeyraTextSecondary)
                        Text(String.format(Locale.US, "%.2f AZN", amountEntered), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VeyraTextPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Xidmət Komissiyası (0.5%):", fontSize = 12.sp, color = VeyraTextSecondary)
                        Text(String.format(Locale.US, "-%.2f AZN", calculatedCommission), fontSize = 12.sp, color = VeyraGold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hesabınıza Köçəcək Yekun:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VeyraTextPrimary)
                        Text(String.format(Locale.US, "%.2f AZN", netPayout), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = VeyraEmerald)
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    onRequestWithdrawal(amountEntered, bankName, ibanText, cardLast4Text, recipientNameText)
                },
                enabled = user?.kycStatus == "Təsdiqləndi" && amountEntered >= 10.0 && amountEntered <= availableBalanceAz,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VeyraEmerald,
                    contentColor = Color(0xFF042017)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                Text(
                    text = "Çıxarış Sorğusunu Göndər",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
