package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.TransactionRowItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    typeFilter: String,
    statusFilter: String,
    onFilterChange: (type: String, status: String) -> Unit,
    selectedTransactionForDetail: TransactionEntity? = null,
    onSelectTransaction: (TransactionEntity?) -> Unit
) {
    val types = listOf("HAMISI", "DEPOZİT", "ÇIXARIŞ", "İNVESTİSİYA", "GƏLİR", "KOMİSSİYA")
    val statuses = listOf("HAMISI", "Tamamlandı", "Emal olunur", "Gözləmədə", "Rədd edildi", "Uğursuz")

    var activeDialogTxn by remember { mutableStateOf<TransactionEntity?>(selectedTransactionForDetail) }

    LaunchedEffect(selectedTransactionForDetail) {
        if (selectedTransactionForDetail != null) {
            activeDialogTxn = selectedTransactionForDetail
        }
    }

    val filteredList = remember(transactions, typeFilter, statusFilter) {
        transactions.filter { txn ->
            val matchType = if (typeFilter == "HAMISI") true else txn.type.equals(typeFilter, ignoreCase = true)
            val matchStatus = if (statusFilter == "HAMISI") true else txn.status.equals(statusFilter, ignoreCase = true)
            matchType && matchStatus
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("transactions_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Əməliyyat Tarixçəsi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                Text(
                    text = "Bütün maliyyə hərəkətlərinin rəsmi mühasibat qeydləri",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary
                    )
                )
            }
        }

        // Type Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Əməliyyat Növü", fontSize = 11.sp, color = VeyraTextMuted)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(types) { type ->
                        val isSelected = typeFilter == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(type, statusFilter) },
                            label = { Text(type, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VeyraEmerald,
                                selectedLabelColor = Color(0xFF042017),
                                containerColor = VeyraNavyElevated,
                                labelColor = VeyraTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) VeyraEmerald else VeyraNavyBorder,
                                enabled = true,
                                selected = isSelected
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // Transactions Container
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    color = VeyraNavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Seçilmiş parametrlər üzrə heç bir əməliyyat tapılmadı.",
                        color = VeyraTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        filteredList.forEachIndexed { index, txn ->
                            TransactionRowItem(
                                transaction = txn,
                                onClick = { activeDialogTxn = txn }
                            )
                            if (index < filteredList.size - 1) {
                                HorizontalDivider(color = VeyraNavyBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Receipt Detail Dialog
    activeDialogTxn?.let { txn ->
        val dateFormatted = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("az")).format(Date(txn.timestampMillis))
        val amountFormatted = String.format(Locale.US, "%.2f AZN", txn.amountCents / 100.0)
        val feeFormatted = String.format(Locale.US, "%.2f AZN", txn.feeCents / 100.0)

        Dialog(onDismissRequest = {
            activeDialogTxn = null
            onSelectTransaction(null)
        }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("receipt_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rəsmi Əməliyyat Qəbzi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = VeyraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Veyra Invest Maliyyə Uçotu",
                        fontSize = 11.sp,
                        color = VeyraTextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = amountFormatted,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = VeyraTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    StatusBadge(status = txn.status)

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = VeyraNavyBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReceiptRow("Əməliyyat ID", txn.transactionIdCode)
                        ReceiptRow("Növü", txn.type)
                        ReceiptRow("Tarix və Vaxt", dateFormatted)
                        ReceiptRow("Ödəniş Metodu", txn.paymentMethod)
                        ReceiptRow("Xidmət Haqqı", feeFormatted)
                        if (txn.referenceId.isNotBlank()) {
                            ReceiptRow("Referans", txn.referenceId)
                        }
                    }

                    if (txn.notesAz.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Qeyd: ${txn.notesAz}",
                            fontSize = 11.sp,
                            color = VeyraTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            activeDialogTxn = null
                            onSelectTransaction(null)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Bağla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = VeyraTextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VeyraTextPrimary)
    }
}
