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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminScreen(
    users: List<UserEntity>,
    products: List<InvestmentProductEntity>,
    pendingDeposits: List<DepositRequestEntity>,
    allDeposits: List<DepositRequestEntity>,
    pendingWithdrawals: List<WithdrawalRequestEntity>,
    allWithdrawals: List<WithdrawalRequestEntity>,
    transactions: List<TransactionEntity>,
    ledgerEntries: List<LedgerEntryEntity>,
    auditLogs: List<AuditLogEntity>,
    onApproveDeposit: (Long) -> Unit,
    onRejectDeposit: (Long, String) -> Unit,
    onApproveWithdrawal: (Long) -> Unit,
    onRejectWithdrawal: (Long, String) -> Unit,
    onToggleUserStatus: (Long, Boolean) -> Unit,
    onSaveProduct: (InvestmentProductEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Depozit Sorğuları") }
    val tabs = listOf(
        "Depozit Sorğuları (${pendingDeposits.size})",
        "Gözləyən Çıxarışlar (${pendingWithdrawals.size})",
        "İstifadəçilər",
        "Məhsul İdarəetməsi",
        "Mühasibat (Ledger)",
        "Audit Jurnalı"
    )

    // Deposit filters
    var depositStatusFilter by remember { mutableStateOf("Hamısı") }
    var viewingReceiptDeposit by remember { mutableStateOf<DepositRequestEntity?>(null) }
    var approvingDeposit by remember { mutableStateOf<DepositRequestEntity?>(null) }
    var rejectingDepositId by remember { mutableStateOf<Long?>(null) }
    var rejectDepositReasonText by remember { mutableStateOf("Ödəniş bank hesabına daxil olmayıb") }

    // Withdrawal reject dialog
    var rejectDialogWithdrawalId by remember { mutableStateOf<Long?>(null) }
    var rejectReasonText by remember { mutableStateOf("Məlumatların uyğunsuzluğu") }

    var editProduct by remember { mutableStateOf<InvestmentProductEntity?>(null) }
    var isNewProductDialog by remember { mutableStateOf(false) }

    // KPI Calculations
    val totalUsersCount = users.size
    val activeUsersCount = users.count { it.isActive }
    val totalDepositSumAz = allDeposits.filter { it.status == "Təsdiqləndi" || it.status == "Tamamlandı" }.sumOf { it.amountCents } / 100.0
    val totalWithdrawalSumAz = allWithdrawals.filter { it.status == "Tamamlandı" }.sumOf { it.amountCents } / 100.0
    val pendingDepositsCount = pendingDeposits.size
    val pendingWithdrawalsCount = pendingWithdrawals.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("admin_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Veyra Admin İdarəetmə Paneli",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = VeyraGold
                        )
                    )
                    Text(
                        text = "Depozit, çıxarış təsdiqi və audit jurnalı",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = VeyraTextSecondary
                        )
                    )
                }

                Surface(
                    color = VeyraGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SUPER ADMIN",
                        color = VeyraGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // KPI Matrix Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Gözləyən Depozitlər",
                        value = "$pendingDepositsCount sorğu",
                        icon = Icons.Default.HourglassTop,
                        color = if (pendingDepositsCount > 0) VeyraGold else VeyraEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Gözləyən Çıxarış",
                        value = "$pendingWithdrawalsCount sorğu",
                        icon = Icons.Default.HourglassBottom,
                        color = if (pendingWithdrawalsCount > 0) VeyraGold else VeyraEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Təsdiqli Depozit",
                        value = String.format(Locale.US, "%.2f ₼", totalDepositSumAz),
                        icon = Icons.Default.ArrowDownward,
                        color = VeyraEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "İstifadəçilər",
                        value = "$activeUsersCount / $totalUsersCount",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Admin Sub-Navigation Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab.startsWith(tab.take(10))
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VeyraGold,
                            selectedLabelColor = Color(0xFF281800),
                            containerColor = VeyraNavyElevated,
                            labelColor = VeyraTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) VeyraGold else VeyraNavyBorder,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // TAB 1: Depozit Sorğuları (Manual Deposit Approvals & Review)
        if (selectedTab.startsWith("Depozit Sorğuları")) {
            // Status Filters (Hamısı, Gözləyir, Təsdiqləndi, Rədd edildi)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Hamısı", "Gözləyir", "Təsdiqləndi", "Rədd edildi").forEach { filter ->
                        val isSel = depositStatusFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) VeyraEmerald else VeyraNavyElevated)
                                .clickable { depositStatusFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color(0xFF042017) else VeyraTextSecondary
                            )
                        }
                    }
                }
            }

            val filteredDeposits = allDeposits.filter { dep ->
                when (depositStatusFilter) {
                    "Gözləyir" -> dep.status == "Gözləyir" || dep.status == "Gözləmədə"
                    "Təsdiqləndi" -> dep.status == "Təsdiqləndi" || dep.status == "Tamamlandı"
                    "Rədd edildi" -> dep.status == "Rədd edildi"
                    else -> true
                }
            }

            if (filteredDeposits.isEmpty()) {
                item {
                    Surface(
                        color = VeyraNavyCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Inbox, contentDescription = null, tint = VeyraTextMuted, modifier = Modifier.size(36.dp))
                            Text(
                                text = "Bu filtrə uyğun heç bir depozit sorğusu yoxdur",
                                color = VeyraTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredDeposits, key = { it.id }) { dep ->
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val isPending = dep.status == "Gözləyir" || dep.status == "Gözləmədə"
                    val isApproved = dep.status == "Təsdiqləndi" || dep.status == "Tamamlandı"
                    val isRejected = dep.status == "Rədd edildi"

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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top Row: Code & Status
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
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = dep.depositIdCode,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = VeyraTextPrimary
                                        )
                                        Text(
                                            text = sdf.format(Date(dep.createdAtMillis)),
                                            fontSize = 10.sp,
                                            color = VeyraTextSecondary
                                        )
                                    }
                                }

                                StatusBadge(status = dep.status)
                            }

                            // Details Table
                            Surface(
                                color = VeyraNavyElevated,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("İstifadəçi:", fontSize = 11.sp, color = VeyraTextMuted)
                                        Text("${dep.userName} (ID: ${dep.userId})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VeyraTextPrimary)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Depozit Məbləği:", fontSize = 11.sp, color = VeyraTextMuted)
                                        Text(
                                            String.format(Locale.US, "%.2f AZN", dep.amountCents / 100.0),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = VeyraEmerald
                                        )
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Ödəniş Hesabı:", fontSize = 11.sp, color = VeyraTextMuted)
                                        Text("Kapital Bank (4169 ... 8363)", fontSize = 11.sp, color = VeyraTextSecondary)
                                    }
                                    if (dep.referenceCode.isNotBlank()) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Qəbz / Ref Kodu:", fontSize = 11.sp, color = VeyraTextMuted)
                                            Text(dep.referenceCode, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VeyraGold)
                                        }
                                    }
                                    if (dep.beforeBalanceCents > 0 || dep.afterBalanceCents > 0) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Balans audit:", fontSize = 10.sp, color = VeyraTextMuted)
                                            Text(
                                                "${dep.beforeBalanceCents / 100.0} ₼ ➔ ${dep.afterBalanceCents / 100.0} ₼",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VeyraTextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            // Receipt Attachment Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = VeyraEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = dep.receiptFileName.ifBlank { "qebz_senedi.jpg" },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VeyraTextPrimary
                                    )
                                }

                                TextButton(
                                    onClick = { viewingReceiptDeposit = dep },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = VeyraEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Çekə bax", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VeyraEmerald)
                                }
                            }

                            // Rejection reason if rejected
                            if (isRejected && dep.rejectionReasonAz.isNotBlank()) {
                                Surface(
                                    color = VeyraError.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = VeyraError, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Səbəb: ${dep.rejectionReasonAz}",
                                            fontSize = 11.sp,
                                            color = VeyraError
                                        )
                                    }
                                }
                            }

                            // Pending Action Buttons: [ Təsdiq Et ] and [ Rədd Et ]
                            if (isPending) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { approvingDeposit = dep },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = VeyraEmerald,
                                            contentColor = Color(0xFF042017)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Təsdiq Et", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            rejectingDepositId = dep.id
                                            rejectDepositReasonText = "Ödəniş bank hesabına daxil olmayıb"
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.5f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraError),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Rədd Et", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: Gözləyən Çıxarışlar
        if (selectedTab.startsWith("Gözləyən Çıxarışlar")) {
            if (pendingWithdrawals.isEmpty()) {
                item {
                    Surface(
                        color = VeyraNavyCard,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Gözləmədə olan heç bir çıxarış sorğusu yoxdur. Bütün sorğular emal edilib.",
                            color = VeyraTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(pendingWithdrawals, key = { it.id }) { withdrawal ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = withdrawal.withdrawalIdCode,
                                    fontWeight = FontWeight.Bold,
                                    color = VeyraTextPrimary,
                                    fontSize = 14.sp
                                )
                                StatusBadge(status = withdrawal.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VeyraNavyElevated, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Alan Şəxs:", fontSize = 11.sp, color = VeyraTextMuted)
                                    Text(withdrawal.recipientFullName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VeyraTextPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Məbləğ / Komissiya:", fontSize = 11.sp, color = VeyraTextMuted)
                                    Text("${withdrawal.amountCents / 100.0} ₼ (Komissiya: ${withdrawal.feeCents / 100.0} ₼)", fontSize = 11.sp, color = VeyraGold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Yekun Köçürüləcək:", fontSize = 11.sp, color = VeyraTextMuted)
                                    Text("${withdrawal.netAmountCents / 100.0} AZN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VeyraEmerald)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Bank / İBAN:", fontSize = 11.sp, color = VeyraTextMuted)
                                    Text("${withdrawal.bankName} (${withdrawal.iban.takeLast(8)}...)", fontSize = 11.sp, color = VeyraTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApproveWithdrawal(withdrawal.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Təsdiq Et", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { rejectDialogWithdrawalId = withdrawal.id },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, VeyraError.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraError),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Rədd Et", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 3: İstifadəçilər
        if (selectedTab == "İstifadəçilər") {
            items(users, key = { it.id }) { u ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VeyraNavyElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = VeyraEmerald)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(u.fullName, fontWeight = FontWeight.Bold, color = VeyraTextPrimary, fontSize = 13.sp)
                            Text("${u.phone} • ${u.email}", fontSize = 11.sp, color = VeyraTextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Balans: ", fontSize = 11.sp, color = VeyraTextMuted)
                                Text(
                                    String.format(Locale.US, "%.2f AZN", u.balanceCents / 100.0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VeyraEmerald
                                )
                                Text(" • KYC: ${u.kycStatus}", fontSize = 11.sp, color = if (u.kycStatus == "Təsdiqləndi") VeyraEmerald else VeyraGold)
                            }
                        }

                        Switch(
                            checked = u.isActive,
                            onCheckedChange = { onToggleUserStatus(u.id, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = VeyraEmerald, checkedTrackColor = VeyraEmerald.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // TAB 4: Məhsul İdarəetməsi
        if (selectedTab == "Məhsul İdarəetməsi") {
            item {
                Button(
                    onClick = {
                        editProduct = InvestmentProductEntity(
                            titleAz = "",
                            categoryAz = "Premium İnvestisiya",
                            minAmountCents = 2500L,
                            maxAmountCents = 5000000L,
                            annualYieldPercent = 14.5,
                            durationDays = 90,
                            riskLevelAz = "Aşağı",
                            withdrawalTermsAz = "Müddət sonunda komissiyasız",
                            descriptionAz = "Veyra Home investisiya məhsulu",
                            drawableResName = "inv_realestate"
                        )
                        isNewProductDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraGold, contentColor = Color(0xFF281800)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yeni İnvestisiya Məhsulu Əlavə Et", fontWeight = FontWeight.Bold)
                }
            }

            items(products, key = { it.id }) { prod ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(prod.titleAz, fontWeight = FontWeight.Bold, color = VeyraTextPrimary, fontSize = 14.sp)
                            Text(prod.categoryAz, fontSize = 11.sp, color = VeyraTextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Min: ${prod.minAmountCents / 100} ₼ • Gəlirlilik: ${prod.annualYieldPercent}% • ${prod.durationDays} gün",
                                fontSize = 11.sp,
                                color = VeyraEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(onClick = { editProduct = prod }) {
                            Icon(Icons.Default.Edit, contentDescription = "Düzəliş et", tint = VeyraGold)
                        }
                    }
                }
            }
        }

        // TAB 5: Mühasibat (Ledger)
        if (selectedTab == "Mühasibat (Ledger)") {
            items(ledgerEntries, key = { it.id }) { entry ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(entry.entryCode, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VeyraTextPrimary)
                            Text(
                                String.format(Locale.US, "%.2f AZN", entry.amountCents / 100.0),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = VeyraEmerald
                            )
                        }
                        Text(
                            "Debit: ${entry.debitAccount}  ➔  Kredit: ${entry.creditAccount}",
                            fontSize = 10.sp,
                            color = VeyraGold
                        )
                        Text(entry.descriptionAz, fontSize = 10.sp, color = VeyraTextSecondary)
                    }
                }
            }
        }

        // TAB 6: Audit Jurnalı
        if (selectedTab == "Audit Jurnalı") {
            items(auditLogs, key = { it.id }) { log ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(log.actionType, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = VeyraGold)
                            Text(sdf.format(Date(log.timestampMillis)), fontSize = 10.sp, color = VeyraTextMuted)
                        }
                        Text("Admin: ${log.adminEmail} • Hədəf: ${log.targetType} (${log.targetId})", fontSize = 10.sp, color = VeyraTextSecondary)
                        Text(log.detailsAz, fontSize = 11.sp, color = VeyraTextPrimary)
                    }
                }
            }
        }
    }

    // Modal Dialog: Approve Deposit Confirmation (Shows before/after calculation)
    approvingDeposit?.let { dep ->
        val user = users.find { it.id == dep.userId }
        val beforeBal = user?.balanceCents ?: 0L
        val afterBal = beforeBal + dep.amountCents

        AlertDialog(
            onDismissRequest = { approvingDeposit = null },
            containerColor = VeyraNavyCard,
            title = {
                Text("Depoziti Təsdiqlə", fontWeight = FontWeight.Bold, color = VeyraEmerald)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Aşağıdakı depozit sorğusunu təsdiqləmək və vəsaiti istifadəçi balansına əlavə etmək istəyirsiniz?",
                        fontSize = 12.sp,
                        color = VeyraTextPrimary
                    )

                    Surface(
                        color = VeyraNavyElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Sorğu Kodu: ${dep.depositIdCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VeyraTextPrimary)
                            Text("İstifadəçi: ${dep.userName} (ID: ${dep.userId})", fontSize = 11.sp, color = VeyraTextSecondary)
                            Text("Əlavə olunacaq: +${String.format(Locale.US, "%.2f AZN", dep.amountCents / 100.0)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VeyraEmerald)
                            Divider(color = VeyraNavyBorder)
                            Text("Əvvəlki balans: ${String.format(Locale.US, "%.2f AZN", beforeBal / 100.0)}", fontSize = 11.sp, color = VeyraTextMuted)
                            Text("Yeni balans: ${String.format(Locale.US, "%.2f AZN", afterBal / 100.0)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VeyraEmerald)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onApproveDeposit(dep.id)
                        approvingDeposit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017))
                ) {
                    Text("Bəli, Təsdiq Et", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { approvingDeposit = null }) {
                    Text("İmtina", color = VeyraTextMuted)
                }
            }
        )
    }

    // Modal Dialog: Reject Deposit with Mandatory Reason
    rejectingDepositId?.let { depId ->
        val reasons = listOf(
            "Ödəniş bank hesabına daxil olmayıb",
            "Qəbz oxunmur və ya etibarsızdır",
            "Məbləğ qəbzdəki məbləğlə uyğun gəlmir",
            "Digər səbəb"
        )

        AlertDialog(
            onDismissRequest = { rejectingDepositId = null },
            containerColor = VeyraNavyCard,
            title = {
                Text("Depozit Sorğusunu Rədd Et", fontWeight = FontWeight.Bold, color = VeyraError)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Zəhmət olmasa rədd etmə səbəbini seçin və ya qeyd edin:", fontSize = 12.sp, color = VeyraTextPrimary)

                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { rejectDepositReasonText = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = rejectDepositReasonText == r,
                                onClick = { rejectDepositReasonText = r },
                                colors = RadioButtonDefaults.colors(selectedColor = VeyraError, unselectedColor = VeyraTextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(r, fontSize = 12.sp, color = VeyraTextPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = rejectDepositReasonText,
                        onValueChange = { rejectDepositReasonText = it },
                        label = { Text("Ətraflı Səbəb", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraError,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectDeposit(depId, rejectDepositReasonText)
                        rejectingDepositId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraError, contentColor = Color.White)
                ) {
                    Text("Rədd Et", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingDepositId = null }) {
                    Text("İmtina", color = VeyraTextMuted)
                }
            }
        )
    }

    // Modal Dialog: View User Receipt Proof
    viewingReceiptDeposit?.let { dep ->
        Dialog(onDismissRequest = { viewingReceiptDeposit = null }) {
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
                        Column {
                            Text(
                                text = "Ödəniş Qəbzi / Çek",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = VeyraTextPrimary
                            )
                            Text(
                                text = "Sorğu: ${dep.depositIdCode}",
                                fontSize = 11.sp,
                                color = VeyraTextSecondary
                            )
                        }
                        IconButton(onClick = { viewingReceiptDeposit = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Bağla", tint = VeyraTextSecondary)
                        }
                    }

                    // Display actual sample receipt drawable
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
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
                            Text(text = "Göndərən: ${dep.userName}", fontSize = 11.sp, color = VeyraTextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "Məbləğ: ${String.format(Locale.US, "%.2f AZN", dep.amountCents / 100.0)}", fontSize = 11.sp, color = VeyraEmerald, fontWeight = FontWeight.Bold)
                            Text(text = "Ref Kodu: ${dep.referenceCode}", fontSize = 10.sp, color = VeyraGold)
                            Text(text = "Fayl: ${dep.receiptFileName}", fontSize = 10.sp, color = VeyraTextSecondary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (dep.status == "Gözləyir" || dep.status == "Gözləmədə") {
                            Button(
                                onClick = {
                                    val d = viewingReceiptDeposit
                                    viewingReceiptDeposit = null
                                    if (d != null) approvingDeposit = d
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Təsdiqə Keç", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = { viewingReceiptDeposit = null },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bağla", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Reject Withdrawal
    rejectDialogWithdrawalId?.let { wId ->
        AlertDialog(
            onDismissRequest = { rejectDialogWithdrawalId = null },
            containerColor = VeyraNavyCard,
            title = {
                Text("Çıxarış Sorğusunu Rədd Et", fontWeight = FontWeight.Bold, color = VeyraError)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("İstifadəçiyə bildiriləcək imtina səbəbini daxil edin:", fontSize = 12.sp, color = VeyraTextPrimary)
                    OutlinedTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        label = { Text("Səbəb", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VeyraError,
                            unfocusedBorderColor = VeyraNavyBorder,
                            focusedTextColor = VeyraTextPrimary,
                            unfocusedTextColor = VeyraTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectWithdrawal(wId, rejectReasonText)
                        rejectDialogWithdrawalId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraError, contentColor = Color.White)
                ) {
                    Text("Rədd Et", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogWithdrawalId = null }) {
                    Text("İmtina", color = VeyraTextMuted)
                }
            }
        )
    }

    // Edit Product Dialog
    editProduct?.let { target ->
        var title by remember { mutableStateOf(target.titleAz) }
        var minAmt by remember { mutableStateOf((target.minAmountCents / 100).toString()) }
        var yield by remember { mutableStateOf(target.annualYieldPercent.toString()) }
        var duration by remember { mutableStateOf(target.durationDays.toString()) }
        var isActive by remember { mutableStateOf(target.isActive) }

        AlertDialog(
            onDismissRequest = {
                isNewProductDialog = false
                editProduct = null
            },
            containerColor = VeyraNavyCard,
            title = {
                Text(
                    text = if (isNewProductDialog) "Yeni İnvestisiya Məhsulu" else "Məhsul Redaktəsi",
                    fontWeight = FontWeight.Bold,
                    color = VeyraGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Məhsulun Adı", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VeyraGold, unfocusedBorderColor = VeyraNavyBorder, focusedTextColor = VeyraTextPrimary, unfocusedTextColor = VeyraTextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = minAmt,
                        onValueChange = { minAmt = it },
                        label = { Text("Minimum Məbləğ (AZN)", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VeyraGold, unfocusedBorderColor = VeyraNavyBorder, focusedTextColor = VeyraTextPrimary, unfocusedTextColor = VeyraTextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = yield,
                        onValueChange = { yield = it },
                        label = { Text("İllik Gəlirlilik Faizi (%)", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VeyraGold, unfocusedBorderColor = VeyraNavyBorder, focusedTextColor = VeyraTextPrimary, unfocusedTextColor = VeyraTextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Müddət (Gün)", color = VeyraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VeyraGold, unfocusedBorderColor = VeyraNavyBorder, focusedTextColor = VeyraTextPrimary, unfocusedTextColor = VeyraTextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Məhsul Aktivdir:", fontSize = 12.sp, color = VeyraTextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val minCents = (minAmt.toLongOrNull() ?: 20L) * 100
                        val yieldDbl = yield.toDoubleOrNull() ?: 14.0
                        val durInt = duration.toIntOrNull() ?: 90
                        val updated = target.copy(
                            titleAz = title.ifBlank { "Yeni Məhsul" },
                            minAmountCents = minCents,
                            annualYieldPercent = yieldDbl,
                            durationDays = durInt,
                            isActive = isActive
                        )
                        onSaveProduct(updated)
                        isNewProductDialog = false
                        editProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VeyraGold, contentColor = Color(0xFF281800))
                ) {
                    Text("Yadda Saxla", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isNewProductDialog = false
                    editProduct = null
                }) {
                    Text("İmtina et", color = VeyraTextMuted)
                }
            }
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = VeyraTextPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = VeyraNavyCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, VeyraNavyBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = VeyraTextMuted)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        }
    }
}
