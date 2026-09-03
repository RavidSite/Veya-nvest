package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.VeyraScreen
import java.util.Locale

@Composable
fun DashboardScreen(
    user: UserEntity,
    userInvestments: List<UserInvestmentEntity>,
    userTransactions: List<TransactionEntity>,
    snapshots: List<PortfolioSnapshotEntity>,
    selectedInterval: String,
    isChartLoading: Boolean,
    products: List<InvestmentProductEntity> = emptyList(),
    onIntervalSelected: (String) -> Unit,
    onDepositClick: () -> Unit,
    onWithdrawalClick: () -> Unit,
    onInvestmentsClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onKycSubmit: (docNo: String, fin: String) -> Unit,
    onTransactionDetailClick: (TransactionEntity) -> Unit,
    onMakeInvestment: ((productId: Long, amountAz: Double) -> Unit)? = null
) {
    var showKycDialog by remember { mutableStateOf(false) }
    var docNoInput by remember { mutableStateOf("") }
    var finCodeInput by remember { mutableStateOf("") }

    // Upgrade Dialog State
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var upgradeTargetTier by remember { mutableStateOf<VeyraHomeTier?>(null) }
    var upgradeAmountInput by remember { mutableStateOf("") }
    var upgradeErrorMessage by remember { mutableStateOf<String?>(null) }

    // Real Calculations from actual Database State
    val availableBalanceAz = user.balanceCents / 100.0
    val totalInvestedAz = userInvestments.filter { it.status == "Aktiv" }.sumOf { it.investedAmountCents } / 100.0
    val totalProfitAz = userInvestments.filter { it.status == "Aktiv" }.sumOf { it.accruedProfitCents } / 100.0
    val totalValuationAz = availableBalanceAz + totalInvestedAz + totalProfitAz

    // Veyra Home Stage Calculation
    val homeCalc = remember(totalInvestedAz) {
        VeyraHomeConfig.calculateHomeStage(totalInvestedAz)
    }

    // Daily change calculation
    val todayChangeAz = if (totalInvestedAz > 0) totalProfitAz * 0.12 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header & KYC Alert if not verified
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Salam, ${user.fullName}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary
                        )
                    )
                    Text(
                        text = user.email.ifBlank { "Veyra Şəxsi Kabinet" },
                        fontSize = 12.sp,
                        color = VeyraGoldLight.copy(alpha = 0.85f)
                    )
                }

                if (user.kycStatus != "Təsdiqləndi") {
                    Button(
                        onClick = { showKycDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VeyraGold.copy(alpha = 0.2f),
                            contentColor = VeyraGold
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("verify_kyc_banner_button")
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("KYC Təsdiqi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 1: "Veyra Home" Hero Visual & Interactive Milestone Stepper
        item {
            VeyraHomeHeroCard(
                homeCalc = homeCalc,
                onUpgradeClick = { targetTier, suggestedAmt ->
                    upgradeTargetTier = targetTier ?: homeCalc.nextTier ?: VeyraHomeConfig.TIERS.first()
                    upgradeAmountInput = if (suggestedAmt > 0) String.format(Locale.US, "%.0f", suggestedAmt) else "25"
                    upgradeErrorMessage = null
                    showUpgradeDialog = true
                },
                onExploreAllTiersClick = onInvestmentsClick
            )
        }

        // Section 2: Big Balance Metric Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraGoldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF141D2D),
                                    Color(0xFF0A0F19)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ümumi Portfel Dəyəri",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = VeyraGoldLight.copy(alpha = 0.9f),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Surface(
                                color = VeyraGoldPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = VeyraGoldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+${String.format(Locale.US, "%.2f", todayChangeAz)} AZN",
                                        color = VeyraGoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Balance Display
                        Text(
                            text = "${String.format(Locale.US, "%,.2f", totalValuationAz)} AZN",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = VeyraTextPrimary,
                                letterSpacing = (-0.5).sp
                            ),
                            modifier = Modifier.testTag("portfolio_total_balance_text")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-metrics Grid (Sərbəst Balans, Yatırılmış Vəsait, Qazanılmış Real Gəlir)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Sərbəst Balans",
                                    fontSize = 11.sp,
                                    color = VeyraTextMuted
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", availableBalanceAz)} AZN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VeyraTextPrimary
                                )
                            }

                            Column {
                                Text(
                                    text = "Yatırılmış Vəsait",
                                    fontSize = 11.sp,
                                    color = VeyraTextMuted
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", totalInvestedAz)} AZN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VeyraTextPrimary
                                )
                            }

                            Column {
                                Text(
                                    text = "Cari Gəlir",
                                    fontSize = 11.sp,
                                    color = VeyraTextMuted
                                )
                                Text(
                                    text = "+${String.format(Locale.US, "%.2f", totalProfitAz)} AZN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VeyraEmerald
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Quick Financial Actions (Depozit, Çıxarış, İnvestisiya)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDepositClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("action_deposit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeyraGoldPrimary,
                        contentColor = Color(0xFF141006)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vəsait artır", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onWithdrawalClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("action_withdrawal_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeyraNavyCard,
                        contentColor = VeyraTextPrimary
                    ),
                    border = BorderStroke(1.dp, VeyraGoldBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowCircleUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = VeyraGoldLight)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Çıxarış", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onInvestmentsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("action_invest_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VeyraNavyCard,
                        contentColor = VeyraTextPrimary
                    ),
                    border = BorderStroke(1.dp, VeyraGoldBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.HomeWork, contentDescription = null, modifier = Modifier.size(18.dp), tint = VeyraGoldLight)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Məhsullar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Section 4: Interactive Dynamic Portfolio Performance Chart
        item {
            PortfolioChart(
                snapshots = snapshots,
                selectedInterval = selectedInterval,
                isLoading = isChartLoading,
                onIntervalSelected = onIntervalSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Section 5: User Active Real Investments
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktiv İnvestisiyalarım",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                TextButton(onClick = onPortfolioClick) {
                    Text("Hamısına bax", color = VeyraEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (userInvestments.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddHome,
                            contentDescription = null,
                            tint = VeyraEmerald,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Hələ aktiv investisiyanız yoxdur",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary
                            )
                        )
                        Text(
                            text = "Veyra Start ilə minimum 25 AZN-dən başlayaraq virtual Veyra Home layihənizin təməlini qoyun.",
                            fontSize = 12.sp,
                            color = VeyraTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = onInvestmentsClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VeyraEmerald,
                                contentColor = Color(0xFF042017)
                            )
                        ) {
                            Text("İnvestisiyalara bax", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(userInvestments.take(3)) { inv ->
                UserInvestmentCard(
                    investment = inv,
                    onDetailsClick = onPortfolioClick
                )
            }
        }

        // Section 6: Recent Real Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Son Əməliyyatlar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                TextButton(onClick = onTransactionsClick) {
                    Text("Tarixçə", color = VeyraEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (userTransactions.isEmpty()) {
            item {
                Text(
                    text = "Heç bir əməliyyat tapılmadı",
                    color = VeyraTextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(userTransactions.take(4)) { txn ->
                TransactionRowItem(
                    transaction = txn,
                    onClick = { onTransactionDetailClick(txn) }
                )
            }
        }
    }

    // KYC Verification Modal Dialog
    if (showKycDialog) {
        AlertDialog(
            onDismissRequest = { showKycDialog = false },
            containerColor = VeyraNavyCard,
            title = {
                Text(
                    text = "Şəxsiyyətin Təsdiqi (KYC)",
                    fontWeight = FontWeight.Bold,
                    color = VeyraTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Maliyyə təhlükəsizliyi qaydalarına əsasən investisiya və çıxarış əməliyyatları üçün şəxsiyyət vəsiqəsi məlumatlarınızı daxil edin.",
                        fontSize = 12.sp,
                        color = VeyraTextSecondary
                    )

                    OutlinedTextField(
                        value = docNoInput,
                        onValueChange = { docNoInput = it.uppercase() },
                        label = { Text("Vəsiqə Seriya və Nömrəsi (məs: AZE12345678)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kyc_doc_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = finCodeInput,
                        onValueChange = { finCodeInput = it.uppercase() },
                        label = { Text("FİN Kod (7 simvol)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kyc_fin_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docNoInput.isNotBlank() && finCodeInput.isNotBlank()) {
                            onKycSubmit(docNoInput, finCodeInput)
                            showKycDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("submit_kyc_btn")
                ) {
                    Text("Təsdiq et", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showKycDialog = false }) {
                    Text("Bağla", color = VeyraTextMuted)
                }
            }
        )
    }

    // Veyra Home Upgrade Modal Dialog
    if (showUpgradeDialog && upgradeTargetTier != null) {
        val target = upgradeTargetTier!!
        val parsedAmt = upgradeAmountInput.toDoubleOrNull() ?: 0.0
        val simulatedTotalInvested = totalInvestedAz + parsedAmt
        val simulatedTargetTier = VeyraHomeConfig.calculateHomeStage(simulatedTotalInvested)

        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            containerColor = VeyraNavyCard,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (target.isElite) Icons.Default.AutoAwesome else Icons.Default.AddHome,
                        contentDescription = null,
                        tint = if (target.isElite) VeyraGold else VeyraEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Veyra Home Yüksəltməsi",
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Target Stage Card Preview
                    Surface(
                        color = if (target.isElite) Color(0xFF281C09) else Color(0xFF132824),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (target.isElite) VeyraGold.copy(alpha = 0.5f) else VeyraEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = target.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (target.isElite) VeyraGoldLight else VeyraTextPrimary
                                )
                                Surface(
                                    color = Color(0x99000000),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Səviyyə ${target.level}",
                                        color = if (target.isElite) VeyraGold else VeyraEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Mərhələ: ${target.stageTitleAz}",
                                fontSize = 12.sp,
                                color = VeyraTextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Hədəf Məbləğ: ${target.minAmountAz.toInt()} AZN",
                                    fontSize = 11.sp,
                                    color = VeyraTextMuted
                                )
                                Text(
                                    text = "+${target.targetYieldPercent}% illik",
                                    fontSize = 11.sp,
                                    color = VeyraEmeraldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Available balance vs investment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sərbəst Balansınız:",
                            fontSize = 12.sp,
                            color = VeyraTextMuted
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.2f", availableBalanceAz)} AZN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (availableBalanceAz >= parsedAmt) VeyraEmerald else VeyraGold
                        )
                    }

                    // Amount Input
                    OutlinedTextField(
                        value = upgradeAmountInput,
                        onValueChange = {
                            upgradeAmountInput = it.filter { c -> c.isDigit() || c == '.' }
                            upgradeErrorMessage = null
                        },
                        label = { Text("İnvestisiya Məbləği (AZN)") },
                        leadingIcon = {
                            Text("₼", color = VeyraEmerald, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upgrade_amount_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraEmerald,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        )
                    )

                    // Quick Chips (+25, +50, +100, +250)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(25.0, 50.0, 100.0, 250.0).forEach { chipAmt ->
                            OutlinedButton(
                                onClick = {
                                    upgradeAmountInput = chipAmt.toInt().toString()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, VeyraNavyBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+${chipAmt.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Simulated result callout
                    if (parsedAmt > 0) {
                        Surface(
                            color = Color(0xFF0F1E1B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(0.8.dp, VeyraNavyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "İnvestisiyadan sonra:",
                                    fontSize = 11.sp,
                                    color = VeyraTextMuted
                                )
                                Text(
                                    text = "🏠 Ümumi: ${String.format(Locale.US, "%.0f", simulatedTotalInvested)} AZN • ${simulatedTargetTier.currentStageTitle}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (simulatedTargetTier.isElite) VeyraGold else VeyraEmeraldLight
                                )
                            }
                        }
                    }

                    if (upgradeErrorMessage != null) {
                        Text(
                            text = upgradeErrorMessage!!,
                            color = VeyraError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "Vəsaitlər real daşınmaz əmlak və infrastruktur layihələrinə yönləndirilir. Gəlir hədəflənən bazar dərəcəsidir.",
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
                            showUpgradeDialog = false
                            onDepositClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VeyraGold, contentColor = Color(0xFF2B1800)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("upgrade_deposit_redirect_btn")
                    ) {
                        Text("Balansı Artır (${parsedAmt.toInt()} AZN)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            val amt = upgradeAmountInput.toDoubleOrNull() ?: 0.0
                            if (amt < 25.0) {
                                upgradeErrorMessage = "Minimum investisiya məbləği 25 AZN-dir."
                                return@Button
                            }
                            if (amt > availableBalanceAz) {
                                upgradeErrorMessage = "Balansınızda kifayət qədər vəsait yoxdur."
                                return@Button
                            }

                            // Match product
                            val targetProduct = products.find { it.titleAz == target.name }
                                ?: products.minByOrNull { kotlin.math.abs((it.minAmountCents / 100.0) - amt) }
                                ?: products.firstOrNull()

                            if (targetProduct != null && onMakeInvestment != null) {
                                onMakeInvestment(targetProduct.id, amt)
                                showUpgradeDialog = false
                            } else if (onMakeInvestment == null) {
                                onInvestmentsClick()
                                showUpgradeDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (target.isElite) VeyraGold else VeyraEmerald,
                            contentColor = if (target.isElite) Color(0xFF281800) else Color(0xFF042017)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_upgrade_btn")
                    ) {
                        Text("Təsdiq et və Yatır", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text("Ləğv et", color = VeyraTextMuted)
                }
            }
        )
    }
}
