package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: (phoneOrEmail: String, otp: String, isGoogle: Boolean) -> Unit,
    errorMessage: String?
) {
    var isPhoneMode by remember { mutableStateOf(true) }
    var phoneNumber by remember { mutableStateOf("+994509876543") }
    var emailAddress by remember { mutableStateOf("rashad@example.com") }
    var otpCode by remember { mutableStateOf("1234") }
    var isOtpSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B11))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1422)),
            border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("auth_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold 3D Emblem
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0C121D))
                        .border(
                            1.5.dp,
                            Brush.sweepGradient(
                                listOf(
                                    VeyraGoldLight,
                                    VeyraGoldPrimary,
                                    VeyraGoldDark,
                                    VeyraGoldLight
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_veyra_gold_emblem),
                        contentDescription = "Veyra Invest Loqo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "V E Y R A   I N V E S T",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = VeyraTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Şəxsi investisiya kabinetinizə təhlükəsiz giriş",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraGoldLight.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = VeyraError.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = VeyraError,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Google Button with gold accent
                OutlinedButton(
                    onClick = { onLoginSuccess("spectrav95@gmail.com", "", true) },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF131B2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_login_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Google",
                        tint = VeyraGoldLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Google ilə daxil ol",
                        color = VeyraTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = VeyraNavyBorder)
                    Text(
                        text = " və ya ",
                        fontSize = 12.sp,
                        color = VeyraTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = VeyraNavyBorder)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phone / Email Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131B2E), RoundedCornerShape(12.dp))
                        .border(1.dp, VeyraGoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isPhoneMode) Brush.horizontalGradient(
                                    listOf(
                                        VeyraGoldDark,
                                        VeyraGoldPrimary
                                    )
                                ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .clickable { isPhoneMode = true; isOtpSent = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Telefon Nömrəsi",
                            fontSize = 12.sp,
                            fontWeight = if (isPhoneMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (isPhoneMode) Color(0xFF151006) else VeyraTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (!isPhoneMode) Brush.horizontalGradient(
                                    listOf(
                                        VeyraGoldDark,
                                        VeyraGoldPrimary
                                    )
                                ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .clickable { isPhoneMode = false; isOtpSent = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Elektron Poçt",
                            fontSize = 12.sp,
                            fontWeight = if (!isPhoneMode) FontWeight.Bold else FontWeight.Medium,
                            color = if (!isPhoneMode) Color(0xFF151006) else VeyraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isPhoneMode) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Telefon Nömrəsi (+994)", color = VeyraTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = VeyraGoldLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraGoldPrimary,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary,
                            focusedContainerColor = Color(0xFF111726),
                            unfocusedContainerColor = Color(0xFF0F1522)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input")
                    )
                } else {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("E-poçt Ünvanı", color = VeyraTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = VeyraGoldLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraGoldPrimary,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary,
                            focusedContainerColor = Color(0xFF111726),
                            unfocusedContainerColor = Color(0xFF0F1522)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )
                }

                if (isOtpSent) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("Təsdiq Kodu (SMS OTP)", color = VeyraTextMuted) },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = VeyraGoldLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraGoldPrimary,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary,
                            focusedContainerColor = Color(0xFF111726),
                            unfocusedContainerColor = Color(0xFF0F1522)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (!isOtpSent) {
                            isOtpSent = true
                        } else {
                            val credential = if (isPhoneMode) phoneNumber else emailAddress
                            onLoginSuccess(credential, otpCode, false)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeyraGoldPrimary,
                        contentColor = Color(0xFF141006)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_auth_button")
                ) {
                    Text(
                        text = if (!isOtpSent) "Təsdiq Kodu Göndər (OTP)" else "Daxil Ol",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

