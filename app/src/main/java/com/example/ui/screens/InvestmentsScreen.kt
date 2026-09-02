package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InvestmentProductEntity
import com.example.data.model.UserEntity
import com.example.data.model.VeyraHomeConfig
import com.example.data.model.VeyraHomeTier
import com.example.ui.components.VeyraHomeProductCard
import com.example.ui.components.rememberDrawableId
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun InvestmentsScreen(
    user: UserEntity?,
    products: List<InvestmentProductEntity>,
    onInvestSubmit: (productId: Long, amountAz: Double) -> Unit,
    onDepositRedirect: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Hamısı") }
    var detailTier by remember { mutableStateOf<VeyraHomeTier?>(null) }
    var investTier by remember { mutableStateOf<VeyraHomeTier?>(null) }
    var investAmountText by remember { mutableStateOf("") }
    var investError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Hamısı", "Təməl (25-100 ₼)", "Residence & Fasad (250-500 ₼)", "Luxury & Elite (750-1200 ₼)")

    val filteredTiers = remember(selectedCategory) {
        when (selectedCategory) {
            "Təməl (25-100 ₼)" -> VeyraHomeConfig.TIERS.filter { it.minAmountAz <= 100.0 }
            "Residence & Fasad (250-500 ₼)" -> VeyraHomeConfig.TIERS.filter { it.minAmountAz in 250.0..500.0 }
            "Luxury & Elite (750-1200 ₼)" -> VeyraHomeConfig.TIERS.filter { it.minAmountAz >= 750.0 }
            else -> VeyraHomeConfig.TIERS
        }
    }

    val availableBalanceAz = (user?.balanceCents ?: 0L) / 100.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("investments_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HomeWork,
                        contentDescription = null,
                        tint = VeyraEmerald,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Veyra Home Məhsulları",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary
                        )
                    )
                }
                Text(
                    text = "Minimum 25 AZN-dən başlayan 8 mərhələli memarlıq və investisiya sistemi",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VeyraEmerald,
                            selectedLabelColor = Color(0xFF141006),
                            containerColor = VeyraNavyCard,
                            labelColor = VeyraTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) VeyraGoldPrimary else VeyraNavyBorder,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // List of Veyra Home Product Cards
        items(filteredTiers) { tier ->
            val matchingProduct = products.find { it.titleAz == tier.name }
            VeyraHomeProductCard(
                tier = tier,
                product = matchingProduct,
                isCurrentActiveStage = false,
                onInvestClick = {
                    investTier = tier
                    investAmountText = tier.minAmountAz.toInt().toString()
                    investError = null
                },
                onDetailClick = {
                    detailTier = tier
                }
            )
        }
    }

    // Detail Dialog
    if (detailTier != null) {
        val tier = detailTier!!
        val context = LocalContext.current
        val imgResId = rememberDrawableId(context, tier.drawableResName)

        Dialog(onDismissRequest = { detailTier = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.2.dp, if (tier.isElite) VeyraGold else VeyraNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Visual
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0A1513))
                    ) {
                        if (imgResId != 0) {
                            Image(
                                painter = painterResource(id = imgResId),
                                contentDescription = tier.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xCC0F1E1B))
                                    )
                                )
                        )
                        Surface(
                            color = Color(0xEE0F1E1B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Səviyyə ${tier.level} • ${tier.stageTitleAz}",
                                color = if (tier.isElite) VeyraGold else VeyraEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = tier.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tier.isElite) VeyraGoldLight else VeyraTextPrimary
                        )
                    )
                    Text(
                        text = tier.detailedDescAz,
                        fontSize = 13.sp,
                        color = VeyraTextSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metrics Grid
                    Surface(
                        color = VeyraNavyElevated,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Min. İnvestisiya:", fontSize = 12.sp, color = VeyraTextMuted)
                                Text("${tier.minAmountAz.toInt()} AZN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VeyraEmerald)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Hədəflənən Gəlirlilik:", fontSize = 12.sp, color = VeyraTextMuted)
                                Text("+${tier.targetYieldPercent}% illik", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VeyraEmeraldLight)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("İnvestisiya Müddəti:", fontSize = 12.sp, color = VeyraTextMuted)
                                Text("${tier.durationDays} gün", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VeyraTextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Status Nişanı:", fontSize = 12.sp, color = VeyraTextMuted)
                                Text(tier.badgeTitleAz, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (tier.isElite) VeyraGold else VeyraTextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Memarlıq və İnfrastruktur Xüsusiyyətləri:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VeyraTextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        tier.features.forEach { feat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = if (tier.isElite) VeyraGold else VeyraEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(feat, fontSize = 12.sp, color = VeyraTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { detailTier = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VeyraNavyBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextPrimary)
                        ) {
                            Text("Bağla")
                        }

                        Button(
                            onClick = {
                                val t = detailTier
                                detailTier = null
                                investTier = t
                                investAmountText = t?.minAmountAz?.toInt()?.toString() ?: "25"
                                investError = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tier.isElite) VeyraGold else VeyraEmerald,
                                contentColor = Color(0xFF141006)
                            )
                        ) {
                            Text("Başla", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Invest Action Dialog
    if (investTier != null) {
        val tier = investTier!!
        val matchingProduct = products.find { it.titleAz == tier.name } ?: products.firstOrNull()
        val parsedAmt = investAmountText.toDoubleOrNull() ?: 0.0

        AlertDialog(
            onDismissRequest = { investTier = null },
            containerColor = VeyraNavyCard,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "${tier.name} — İnvestisiya",
                    fontWeight = FontWeight.Bold,
                    color = if (tier.isElite) VeyraGoldLight else VeyraTextPrimary,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Mərhələ: ${tier.stageTitleAz} (${tier.minAmountAz.toInt()} AZN)",
                        fontSize = 13.sp,
                        color = VeyraTextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sərbəst Balansınız:", fontSize = 12.sp, color = VeyraTextMuted)
                        Text(
                            text = "${String.format(Locale.US, "%.2f", availableBalanceAz)} AZN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (availableBalanceAz >= parsedAmt) VeyraEmerald else VeyraGold
                        )
                    }

                    OutlinedTextField(
                        value = investAmountText,
                        onValueChange = {
                            investAmountText = it.filter { c -> c.isDigit() || c == '.' }
                            investError = null
                        },
                        label = { Text("Məbləğ (AZN)") },
                        leadingIcon = {
                            Text("₼", color = VeyraEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invest_amount_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        )
                    )

                    // Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(tier.minAmountAz, tier.minAmountAz * 2, tier.minAmountAz * 4).forEach { amtVal ->
                            OutlinedButton(
                                onClick = { investAmountText = amtVal.toInt().toString() },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, VeyraNavyBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextPrimary),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("${amtVal.toInt()} ₼", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (investError != null) {
                        Text(
                            text = investError!!,
                            color = VeyraError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "Vəsaitlər real daşınmaz əmlak və infrastruktur aktivlərinə yatırılır. Gəlirlilik bazar konyunkturasına uyğun hədəflənən göstəricidir.",
                        fontSize = 10.sp,
                        color = VeyraTextMuted,
                        lineHeight = 14.sp
                    )
                }
            },
            confirmButton = {
                if (availableBalanceAz < parsedAmt) {
                    Button(
                        onClick = {
                            investTier = null
                            onDepositRedirect()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VeyraGold, contentColor = Color(0xFF281800)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("invest_deposit_redirect_btn")
                    ) {
                        Text("Balansı Artır (${parsedAmt.toInt()} AZN)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            val amt = investAmountText.toDoubleOrNull() ?: 0.0
                            if (amt < tier.minAmountAz) {
                                investError = "Minimum investisiya məbləği ${tier.minAmountAz.toInt()} AZN-dir."
                                return@Button
                            }
                            if (amt > availableBalanceAz) {
                                investError = "Balansınızda kifayət qədər vəsait yoxdur."
                                return@Button
                            }

                            val pId = matchingProduct?.id ?: 1L
                            onInvestSubmit(pId, amt)
                            investTier = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tier.isElite) VeyraGold else VeyraEmerald,
                            contentColor = if (tier.isElite) Color(0xFF281800) else Color(0xFF042017)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_invest_btn")
                    ) {
                        Text("İnvestisiya Et", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { investTier = null }) {
                    Text("Ləğv et", color = VeyraTextMuted)
                }
            }
        )
    }
}
