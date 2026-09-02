package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.DepositRequestEntity
import com.example.data.model.PaymentCardEntity
import com.example.data.model.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepositScreen(
    user: UserEntity?,
    userDeposits: List<DepositRequestEntity>,
    officialCard: PaymentCardEntity?,
    userSavedCards: List<PaymentCardEntity> = emptyList(),
    onSubmitManualDeposit: (amountAz: Double, refCode: String, receiptName: String, receiptUri: String, paymentDateMillis: Long) -> Unit,
    onSavePaymentCard: (bankName: String, cardNumber: String, cardHolder: String, expiry: String, cvv: String) -> Unit,
    isProcessing: Boolean,
    errorMessage: String?,
    infoMessage: String?,
    onGoToTransactions: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Depozit Et") } // "Depozit Et" or "Depozitlərim"

    // Amount Selection
    val presetAmounts = listOf(25, 50, 100, 250, 500, 750, 1000, 1200)
    var selectedPreset by remember { mutableStateOf<Int?>(100) }
    var customAmountInput by remember { mutableStateOf("") }
    var isCustomAmountSelected by remember { mutableStateOf(false) }

    val currentAmountAz: Double = if (isCustomAmountSelected) {
        customAmountInput.toDoubleOrNull() ?: 0.0
    } else {
        selectedPreset?.toDouble() ?: 0.0
    }

    // Step state in Deposit flow:
    // 1 = Account Info & Amount, 2 = "Ödəniş etdim" -> 60s processing & Receipt upload, 3 = Completed/Submitted
    var depositStep by remember { mutableStateOf(1) }
    var hasCopiedCard by remember { mutableStateOf(false) }

    // Step 2 Form fields
    var referenceInput by remember { mutableStateOf("") }
    var selectedReceiptType by remember { mutableStateOf("img_receipt_sample.jpg") }
    var receiptAttachedName by remember { mutableStateOf("birbank_qebz_${System.currentTimeMillis().toString().takeLast(5)}.jpg") }
    var paymentDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showReceiptPreviewDialog by remember { mutableStateOf(false) }

    // 60-Second Countdown Animation State
    var countdownSeconds by remember { mutableStateOf(60) }
    var isCountdownActive by remember { mutableStateOf(false) }

    LaunchedEffect(depositStep) {
        if (depositStep == 2) {
            countdownSeconds = 60
            isCountdownActive = true
            while (countdownSeconds > 0 && isCountdownActive) {
                delay(1000)
                countdownSeconds--
            }
        } else {
            isCountdownActive = false
        }
    }

    // Reset copied notification after delay
    LaunchedEffect(hasCopiedCard) {
        if (hasCopiedCard) {
            delay(3000)
            hasCopiedCard = false
        }
    }

    val officialCardNumber = officialCard?.cardNumber ?: "4169738849528363"
    val officialCardFormatted = officialCardNumber.chunked(4).joinToString(" ")
    val officialCardHolder = officialCard?.cardHolder ?: "VEYRA INVEST MMC / ÖDƏNİŞ HESABI"
    val officialExpiry = officialCard?.expiryMonthYear ?: "12/28"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("deposit_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Navigation Switcher (Depozit Et / Depozitlərim)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(VeyraNavyCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Depozit Et", "Depozitlərim (${userDeposits.size})").forEach { tab ->
                    val isSelected = selectedTab.startsWith(tab.take(8))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VeyraEmerald else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFF141006) else VeyraTextSecondary
                        )
                    }
                }
            }
        }

        // Global Error / Info banners
        if (errorMessage != null) {
            item {
                Surface(
                    color = VeyraError.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = VeyraError, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = errorMessage, color = VeyraError, fontSize = 12.sp)
                    }
                }
            }
        }

        if (infoMessage != null) {
            item {
                Surface(
                    color = VeyraEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VeyraEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = infoMessage, color = VeyraEmerald, fontSize = 12.sp)
                    }
                }
            }
        }

        if (selectedTab.startsWith("Depozit Et")) {
            // STEP 1: Amount Selection & Official Account Details
            if (depositStep == 1) {
                // Header
                item {
                    Column {
                        Text(
                            text = "Hesabınıza vəsait əlavə edin",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Milli bank kartı və ya Birbank vasitəsilə təhlükəsiz manual depozit",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = VeyraTextSecondary
                            )
                        )
                    }
                }

                // Amount Selection Matrix
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Məbləğ seçimi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = VeyraTextPrimary
                                )
                                Text(
                                    text = "Min: 25 AZN",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = VeyraGold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Grid of 8 preset amounts
                            val rows = presetAmounts.chunked(4)
                            rows.forEach { rowList ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowList.forEach { amt ->
                                        val isSelected = !isCustomAmountSelected && selectedPreset == amt
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) VeyraEmerald else VeyraNavyElevated)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) VeyraEmerald else VeyraNavyBorder,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    isCustomAmountSelected = false
                                                    selectedPreset = amt
                                                }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$amt",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color(0xFF141006) else VeyraTextPrimary
                                                )
                                                Text(
                                                    text = "AZN",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isSelected) Color(0xFF141006).copy(alpha = 0.8f) else VeyraTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // "Digər məbləğ" option
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isCustomAmountSelected) VeyraNavyElevated else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isCustomAmountSelected) VeyraEmerald else VeyraNavyBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        isCustomAmountSelected = true
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isCustomAmountSelected,
                                            onClick = { isCustomAmountSelected = true },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = VeyraEmerald,
                                                unselectedColor = VeyraTextMuted
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Digər məbləğ",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = VeyraTextPrimary
                                        )
                                    }

                                    if (isCustomAmountSelected) {
                                        OutlinedTextField(
                                            value = customAmountInput,
                                            onValueChange = { customAmountInput = it },
                                            placeholder = { Text("0.00", color = VeyraTextMuted, fontSize = 13.sp) },
                                            trailingIcon = {
                                                Text("AZN", color = VeyraEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                                            },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = VeyraEmerald,
                                                unfocusedBorderColor = VeyraNavyBorder,
                                                focusedTextColor = VeyraTextPrimary,
                                                unfocusedTextColor = VeyraTextPrimary
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .width(140.dp)
                                                .height(48.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Official Payment Account Display (Birbank / Kapital Bank Card Mockup)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ödəniş Hesabı Rekvizitləri",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = VeyraTextPrimary
                            )
                            Surface(
                                color = Color(0xFFE50914).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "KAPİTAL BANK / BIRBANK",
                                    color = Color(0xFFFF5252),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Premium Birbank / Kapital Bank Card Visual
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(22.dp), ambientColor = Color(0xFFE50914)),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF8B0000), // Deep Birbank Crimson
                                                Color(0xFFE50914), // Kapital Red
                                                Color(0xFF3B0000)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // Top Header: Bank branding + Contactless wave
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Red Kapital Bank / Birbank style emblem
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "B",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 18.sp,
                                                    color = Color(0xFFE50914)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Birbank",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "Kapital Bank",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.White.copy(alpha = 0.8f)
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Contactless,
                                                contentDescription = "Contactless",
                                                tint = Color.White.copy(alpha = 0.9f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = Color.White.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "TƏSDİQLİ HESAB",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Gold EMV Chip
                                    Box(
                                        modifier = Modifier
                                            .size(width = 38.dp, height = 28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFFFD700),
                                                        Color(0xFFE6AC00),
                                                        Color(0xFFFFF3A1)
                                                    )
                                                )
                                            )
                                            .border(1.dp, Color(0xFFB8860B), RoundedCornerShape(6.dp))
                                    )

                                    // Card Number
                                    Column {
                                        Text(
                                            text = "KART / HESAB NÖMRƏSİ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White.copy(alpha = 0.7f),
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = officialCardFormatted,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            letterSpacing = 2.sp
                                        )
                                    }

                                    // Card Holder & Expiry & VISA
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column {
                                            Text(
                                                text = "HESAB SAHİBİ",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = officialCardHolder,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "BİTMƏ",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = officialExpiry,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = "VISA",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Copy Button & Copy Feedback Toast
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Veyra Invest Card", officialCardNumber)
                                    clipboard.setPrimaryClip(clip)
                                    hasCopiedCard = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VeyraNavyElevated,
                                    contentColor = VeyraEmerald
                                ),
                                border = BorderStroke(1.dp, VeyraEmerald.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hasCopiedCard) "Məlumat kopyalandı!" else "Məlumatı kopyala",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Mandatory Official Warning
                        Surface(
                            color = Color(0xFF2E1A05),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, VeyraGold.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = VeyraGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Qeyd və Xəbərdarlıq",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = VeyraGold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "“Ödənişi yalnız göstərilən hesaba göndərin. Digər hesablara edilən ödənişlərə görə Veyra Invest məsuliyyət daşımır.”",
                                        fontSize = 11.sp,
                                        color = VeyraTextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Proceed Button "Ödəniş etdim"
                item {
                    val isValidAmount = currentAmountAz >= 25.0
                    Button(
                        onClick = {
                            if (isValidAmount) {
                                depositStep = 2
                            }
                        },
                        enabled = isValidAmount,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VeyraEmerald,
                            contentColor = Color(0xFF141006)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("i_paid_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isValidAmount) "Ödəniş etdim (${String.format(Locale.US, "%.2f", currentAmountAz)} AZN)" else "Minimum 25 AZN seçin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // STEP 2: 60-Second Countdown Animation & Payment Proof Upload Form
            if (depositStep == 2) {
                // Header & Back to Step 1
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { depositStep = 1 }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = VeyraTextPrimary)
                        }
                        Text(
                            text = "Ödəniş Təsdiqi və Çek Təqdimatı",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VeyraTextPrimary
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }

                // Dynamic 60-Second Countdown & Progress Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(1.dp, VeyraEmerald.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Circular Countdown & Pulse Animation
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(90.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { (60 - countdownSeconds) / 60f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = VeyraEmerald,
                                    strokeWidth = 6.dp,
                                    trackColor = VeyraNavyElevated,
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$countdownSeconds",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp,
                                        color = VeyraTextPrimary
                                    )
                                    Text(
                                        text = "saniyə",
                                        fontSize = 10.sp,
                                        color = VeyraTextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ödəniş sistem tərəfindən yoxlanılır...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = VeyraEmerald,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Bank tranzaksiyasının təsdiqlənməsi üçün qəbz məlumatlarınızı daxil edin",
                                    fontSize = 11.sp,
                                    color = VeyraTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Payment Proof Upload Form (Qəbz təqdimatı)
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
                            Text(
                                text = "Ödəniş Məlumatları",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = VeyraTextPrimary
                            )

                            // Amount Display (Fixed)
                            OutlinedTextField(
                                value = String.format(Locale.US, "%.2f AZN", currentAmountAz),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Məbləğ", color = VeyraTextMuted) },
                                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = VeyraEmerald) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VeyraNavyBorder,
                                    unfocusedBorderColor = VeyraNavyBorder,
                                    focusedTextColor = VeyraTextPrimary,
                                    unfocusedTextColor = VeyraTextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Payment Date & Time
                            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            OutlinedTextField(
                                value = sdf.format(Date(paymentDateMillis)),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Ödəniş Tarixi və Saatı", color = VeyraTextMuted) },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = VeyraGold) },
                                trailingIcon = {
                                    IconButton(onClick = { paymentDateMillis = System.currentTimeMillis() }) {
                                        Icon(Icons.Default.Refresh, contentDescription = "İndi", tint = VeyraEmerald)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VeyraNavyBorder,
                                    unfocusedBorderColor = VeyraNavyBorder,
                                    focusedTextColor = VeyraTextPrimary,
                                    unfocusedTextColor = VeyraTextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Reference / Transaction Number (Kapital / Birbank Qəbz Nömrəsi)
                            OutlinedTextField(
                                value = referenceInput,
                                onValueChange = { referenceInput = it },
                                label = { Text("Əməliyyat / Qəbz Nömrəsi (İxtiyari)", color = VeyraTextMuted) },
                                placeholder = { Text("Məs: KAP-98421 və ya RRN", color = VeyraTextMuted, fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = VeyraTextSecondary) },
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

                            // Receipt Upload / Selection Box (PNG / JPG / PDF)
                            Text(
                                text = "Ödəniş Çeki / Qəbz (PNG / JPG / PDF)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = VeyraTextPrimary
                            )

                            Surface(
                                color = VeyraNavyElevated,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, VeyraEmerald.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showReceiptPreviewDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(VeyraEmerald.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ReceiptLong,
                                                contentDescription = null,
                                                tint = VeyraEmerald,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = receiptAttachedName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = VeyraTextPrimary
                                            )
                                            Text(
                                                text = "Birbank elektron qəbzi qoşuldu (245 KB)",
                                                fontSize = 10.sp,
                                                color = VeyraEmerald
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showReceiptPreviewDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = VeyraEmerald.copy(alpha = 0.2f),
                                            contentColor = VeyraEmerald
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("Bax / Dəyiş", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Receipt Note
                            Text(
                                text = "“Çeki yükləməyiniz ödənişinizin daha tez təsdiqlənməsinə kömək edəcək.”",
                                fontSize = 11.sp,
                                color = VeyraGold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Submit Request Button
                item {
                    Button(
                        onClick = {
                            val finalRef = referenceInput.ifBlank { "KAP-" + (100000..999999).random() }
                            onSubmitManualDeposit(
                                currentAmountAz,
                                finalRef,
                                receiptAttachedName,
                                "drawable/img_receipt_sample",
                                paymentDateMillis
                            )
                            depositStep = 3
                        },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VeyraEmerald,
                            contentColor = Color(0xFF141006)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("submit_manual_deposit_request")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color(0xFF141006), modifier = Modifier.size(22.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sorğunu təsdiqlə və göndər",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // STEP 3: Submitted & Pending State (Status: Gözləyir)
            if (depositStep == 3) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(1.dp, VeyraGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(VeyraGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = VeyraGold,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Text(
                                text = "Depozit Sorğunuz Qəbul Edildi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = VeyraTextPrimary,
                                textAlign = TextAlign.Center
                            )

                            StatusBadge(status = "Gözləyir")

                            Text(
                                text = "Sorğunuz moderatorlarımız tərəfindən yoxlanılır. Ödəniş bank hesabımıza çatdıqdan dərhal sonra balansınıza əlavə olunacaq.",
                                fontSize = 12.sp,
                                color = VeyraTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Divider(color = VeyraNavyBorder)

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DetailRow(label = "Seçilən Məbləğ:", value = String.format(Locale.US, "%.2f AZN", currentAmountAz))
                                DetailRow(label = "Ödəniş Hesabı:", value = "Kapital Bank (4169 ... 8363)")
                                DetailRow(label = "Status:", value = "🟡 Gözləyir")
                                DetailRow(label = "Qəbz:", value = receiptAttachedName)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        depositStep = 1
                                        selectedTab = "Depozitlərim (${userDeposits.size})"
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VeyraEmerald,
                                        contentColor = Color(0xFF042017)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Depozitlərimə bax", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        depositStep = 1
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextPrimary),
                                    border = BorderStroke(1.dp, VeyraNavyBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Yeni depozit", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: "Depozitlərim" (User's Deposit History)
        if (selectedTab.startsWith("Depozitlərim")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Depozit Tarixçəsi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = VeyraTextPrimary
                        )
                        Text(
                            text = "Göndərilmiş bütün depozit sorğularınızın real vaxt statusu",
                            fontSize = 11.sp,
                            color = VeyraTextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            selectedTab = "Depozit Et"
                            depositStep = 1
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VeyraEmerald,
                            contentColor = Color(0xFF042017)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Depozit et", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (userDeposits.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = VeyraTextMuted, modifier = Modifier.size(48.dp))
                            Text(
                                text = "Hələ ki depozit sorğunuz yoxdur",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = VeyraTextPrimary
                            )
                            Text(
                                text = "Kapital Bank / Birbank vasitəsilə 25 AZN-dən başlayan ilk depozitinizi edin.",
                                fontSize = 11.sp,
                                color = VeyraTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    selectedTab = "Depozit Et"
                                    depositStep = 1
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017))
                            ) {
                                Text("İndi Depozit Et", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(userDeposits) { deposit ->
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val isPending = deposit.status == "Gözləyir" || deposit.status == "Gözləmədə"
                    val isApproved = deposit.status == "Təsdiqləndi" || deposit.status == "Tamamlandı"
                    val isRejected = deposit.status == "Rədd edildi"

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(
                            1.dp,
                            when {
                                isApproved -> VeyraEmerald.copy(alpha = 0.4f)
                                isRejected -> VeyraError.copy(alpha = 0.4f)
                                else -> VeyraGold.copy(alpha = 0.4f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isApproved -> VeyraEmerald.copy(alpha = 0.2f)
                                                    isRejected -> VeyraError.copy(alpha = 0.2f)
                                                    else -> VeyraGold.copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isApproved -> Icons.Default.CheckCircle
                                                isRejected -> Icons.Default.Cancel
                                                else -> Icons.Default.HourglassTop
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isApproved -> VeyraEmerald
                                                isRejected -> VeyraError
                                                else -> VeyraGold
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = deposit.depositIdCode,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = VeyraTextPrimary
                                        )
                                        Text(
                                            text = sdf.format(Date(deposit.createdAtMillis)),
                                            fontSize = 10.sp,
                                            color = VeyraTextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+${String.format(Locale.US, "%.2f", deposit.amountCents / 100.0)} AZN",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = when {
                                            isApproved -> VeyraEmerald
                                            isRejected -> VeyraTextMuted
                                            else -> VeyraGold
                                        }
                                    )
                                    StatusBadge(status = deposit.status)
                                }
                            }

                            Divider(color = VeyraNavyBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Metod: ${deposit.paymentMethod.take(24)}",
                                    fontSize = 11.sp,
                                    color = VeyraTextSecondary
                                )
                                if (deposit.referenceCode.isNotBlank()) {
                                    Text(
                                        text = "Ref: ${deposit.referenceCode}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VeyraTextPrimary
                                    )
                                }
                            }

                            // Rejection Reason display if rejected
                            if (isRejected && deposit.rejectionReasonAz.isNotBlank()) {
                                Surface(
                                    color = VeyraError.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = VeyraError, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Rədd səbəbi: ${deposit.rejectionReasonAz}",
                                            fontSize = 11.sp,
                                            color = VeyraError
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Receipt Preview & Selection Dialog
    if (showReceiptPreviewDialog) {
        Dialog(onDismissRequest = { showReceiptPreviewDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraEmerald.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ödəniş Qəbzi (Preview)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = VeyraTextPrimary
                        )
                        IconButton(onClick = { showReceiptPreviewDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Bağla", tint = VeyraTextSecondary)
                        }
                    }

                    // Display actual sample receipt drawable
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_receipt_sample),
                            contentDescription = "Birbank Qəbzi",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Surface(
                        color = VeyraNavyElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Fayl: $receiptAttachedName", fontSize = 11.sp, color = VeyraTextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "Format: JPEG / PNG • Təsdiqli bank sənədi", fontSize = 10.sp, color = VeyraEmerald)
                            Text(text = "Məbləğ: ${String.format(Locale.US, "%.2f AZN", currentAmountAz)}", fontSize = 10.sp, color = VeyraTextSecondary)
                        }
                    }

                    Button(
                        onClick = { showReceiptPreviewDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Çeki Təsdiq Et", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = VeyraTextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VeyraTextPrimary)
    }
}
