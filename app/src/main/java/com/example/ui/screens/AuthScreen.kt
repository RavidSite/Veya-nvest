package com.example.ui.screens

import android.accounts.AccountManager
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
    onLoginSuccess: (email: String, fullName: String?, isGoogle: Boolean) -> Unit,
    errorMessage: String?
) {
    var emailAddress by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var isCheckingGoogle by remember { mutableStateOf(false) }

    // Android native Google Account Picker launcher
    val googleAccountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isCheckingGoogle = false
        if (result.resultCode == Activity.RESULT_OK) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                onLoginSuccess(accountName, null, true)
            } else {
                localError = "Google hesabı seçilmədi. Zəhmət olmasa Gmail ünvanınızı daxil edin."
            }
        }
    }

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
                .verticalScroll(rememberScrollState())
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
                    text = "Rəsmi Google / Gmail hesabı ilə təhlükəsiz giriş",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraGoldLight.copy(alpha = 0.85f)
                    ),
                    textAlign = TextAlign.Center
                )

                val displayError = errorMessage ?: localError
                if (displayError != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = VeyraError.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = displayError,
                            color = VeyraError,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 1. Google ile daxil ol duymesi (Cihazdaki real hesablari acir)
                Button(
                    onClick = {
                        localError = null
                        isCheckingGoogle = true
                        try {
                            val intent = AccountManager.newChooseAccountIntent(
                                null,
                                null,
                                arrayOf("com.google"),
                                false,
                                null,
                                null,
                                null,
                                null
                            )
                            googleAccountPickerLauncher.launch(intent)
                        } catch (e: Exception) {
                            isCheckingGoogle = false
                            localError = "Cihazda qeydiyyatlı Google hesabı tapılmadı. Zəhmət olmasa aşağıdakı xanaya öz Gmail ünvanınızı qeyd edin."
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131B2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("google_login_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF4285F4)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        if (isCheckingGoogle) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = VeyraGoldLight,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hesab yoxlanılır...",
                                color = VeyraTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "Google Hesabı ilə Daxil Ol",
                                color = VeyraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = VeyraNavyBorder)
                    Text(
                        text = " və ya birbaşa Gmail ilə ",
                        fontSize = 12.sp,
                        color = VeyraTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = VeyraNavyBorder)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ad Soyad (İsteğe bağlı)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Adınız və Soyadınız (istəyə bağlı)", color = VeyraTextMuted, fontSize = 13.sp) },
                    placeholder = { Text("Məs: Rəşad Əliyev", color = VeyraTextMuted.copy(alpha = 0.5f), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = VeyraGoldLight) },
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
                        .testTag("fullname_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Həqiqi Gmail Ünvanı xanası
                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { 
                        emailAddress = it
                        localError = null
                    },
                    label = { Text("Gmail Ünvanınız", color = VeyraTextMuted, fontSize = 13.sp) },
                    placeholder = { Text("ad.soyad@gmail.com", color = VeyraTextMuted.copy(alpha = 0.5f), fontSize = 13.sp) },
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = VeyraGoldLight.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Real investor kabinetiniz bu Gmail ilə qorunur",
                        fontSize = 11.sp,
                        color = VeyraTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Təsdiq və daxil ol düyməsi
                Button(
                    onClick = {
                        val clean = emailAddress.trim()
                        if (clean.isBlank()) {
                            localError = "Zəhmət olmasa Gmail ünvanınızı daxil edin."
                            return@Button
                        }
                        if (!clean.contains("@") || !clean.contains(".")) {
                            localError = "Zəhmət olmasa düzgün formatda Gmail ünvanı yazın (məs: ad.soyad@gmail.com)."
                            return@Button
                        }
                        localError = null
                        onLoginSuccess(clean, fullName.ifBlank { null }, false)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeyraGoldPrimary,
                        contentColor = Color(0xFF141006)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_auth_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Gmail ilə Daxil Ol",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF141006)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF141006),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
