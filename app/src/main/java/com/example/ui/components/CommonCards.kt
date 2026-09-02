package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvestmentProductEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserInvestmentEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Tamamlandı", "Təsdiqləndi", "Aktiv" -> Color(0xFF133E35) to Color(0xFF34D399)
        "Emal olunur", "Gözləmədə", "Yoxlanılır" -> Color(0xFF3B2706) to Color(0xFFFBBF24)
        "Uğursuz", "Rədd edildi", "Ləğv edildi" -> Color(0xFF3D1616) to Color(0xFFF87171)
        else -> Color(0xFF1B2E2A) to Color(0xFFA8BDB5)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RiskBadge(riskLevel: String) {
    val (color, text) = when (riskLevel) {
        "Aşağı" -> VeyraEmerald to "Aşağı Risk"
        "Orta" -> VeyraGold to "Orta Risk"
        else -> VeyraError to "Yüksək Risk"
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProductCard(
    product: InvestmentProductEntity,
    onInvestClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = rememberDrawableId(context, product.drawableResName)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
        border = BorderStroke(1.dp, VeyraGoldBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = product.titleAz,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(VeyraNavyElevated, VeyraNavyBorder)
                                )
                            )
                    )
                }

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VeyraNavyCard.copy(alpha = 0.95f))
                            )
                        )
                )

                // Category & Risk Tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xCC0F1E1B),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = product.categoryAz,
                            color = VeyraTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    RiskBadge(riskLevel = product.riskLevelAz)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.titleAz,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.descriptionAz,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats grid: Min Məbləğ | İllik Gəlirlilik | Müddət
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Min. Məbləğ", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            "${product.minAmountCents / 100} AZN",
                            fontWeight = FontWeight.Bold,
                            color = VeyraEmerald,
                            fontSize = 14.sp
                        )
                    }
                    Column {
                        Text("Gəlirlilik (İllik)", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            "+${product.annualYieldPercent}%",
                            fontWeight = FontWeight.Bold,
                            color = VeyraGold,
                            fontSize = 14.sp
                        )
                    }
                    Column {
                        Text("Müddət", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            "${product.durationDays} gün",
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetailClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, VeyraNavyBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraTextSecondary),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_button_${product.id}")
                    ) {
                        Text("Ətraflı", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onInvestClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VeyraEmerald,
                            contentColor = Color(0xFF042017)
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("invest_button_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("İnvestisiya et", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun UserInvestmentCard(
    investment: UserInvestmentEntity,
    onDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = rememberDrawableId(context, investment.drawableResName)
    val investedAz = investment.investedAmountCents / 100.0
    val currentValuationAz = investment.currentValuationCents / 100.0
    val profitAz = investment.accruedProfitCents / 100.0
    val profitPercent = if (investedAz > 0) (profitAz / investedAz) * 100.0 else 0.0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
        border = BorderStroke(1.dp, VeyraGoldBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() }
            .testTag("user_investment_${investment.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VeyraNavyElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = VeyraEmerald)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = investment.productTitleAz,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "İllik gəlirlilik: +${investment.annualYieldPercent}%",
                        fontSize = 12.sp,
                        color = VeyraTextSecondary
                    )
                }

                StatusBadge(status = investment.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real Valuation Matrix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("İnvestisiya", fontSize = 11.sp, color = VeyraTextMuted)
                    Text(
                        String.format(Locale.US, "%.2f AZN", investedAz),
                        fontWeight = FontWeight.SemiBold,
                        color = VeyraTextPrimary,
                        fontSize = 13.sp
                    )
                }
                Column {
                    Text("Cari Dəyər", fontSize = 11.sp, color = VeyraTextMuted)
                    Text(
                        String.format(Locale.US, "%.2f AZN", currentValuationAz),
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary,
                        fontSize = 13.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Real Gəlir", fontSize = 11.sp, color = VeyraTextMuted)
                    Text(
                        String.format(Locale.US, "+%.2f AZN (%.2f%%)", profitAz, profitPercent),
                        fontWeight = FontWeight.Bold,
                        color = VeyraEmerald,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val isPositive = transaction.type in listOf("DEPOZİT", "GƏLİR", "DÜZƏLİŞ")
    val amountFormatted = String.format(Locale.US, "%.2f AZN", transaction.amountCents / 100.0)
    val dateFormatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(transaction.timestampMillis))

    val icon = when (transaction.type) {
        "DEPOZİT" -> Icons.Default.ArrowDownward
        "ÇIXARIŞ" -> Icons.Default.ArrowUpward
        "İNVESTİSİYA" -> Icons.Default.TrendingUp
        "GƏLİR" -> Icons.Default.MonetizationOn
        "KOMİSSİYA" -> Icons.Default.Receipt
        else -> Icons.Default.SyncAlt
    }

    val iconBg = when (transaction.type) {
        "DEPOZİT", "GƏLİR" -> VeyraEmerald.copy(alpha = 0.15f)
        "ÇIXARIŞ" -> VeyraError.copy(alpha = 0.15f)
        "İNVESTİSİYA" -> VeyraGold.copy(alpha = 0.15f)
        else -> VeyraNavyElevated
    }

    val iconTint = when (transaction.type) {
        "DEPOZİT", "GƏLİR" -> VeyraEmerald
        "ÇIXARIŞ" -> VeyraError
        "İNVESTİSİYA" -> VeyraGold
        else -> VeyraTextSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = transaction.type,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.notesAz.ifBlank { transaction.type },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = VeyraTextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dateFormatted,
                    fontSize = 11.sp,
                    color = VeyraTextMuted
                )
                Text(" • ", fontSize = 11.sp, color = VeyraTextMuted)
                Text(
                    text = transaction.paymentMethod,
                    fontSize = 11.sp,
                    color = VeyraTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isPositive) "+$amountFormatted" else "-$amountFormatted",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isPositive) VeyraEmerald else VeyraTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            StatusBadge(status = transaction.status)
        }
    }
}

fun rememberDrawableId(context: android.content.Context, name: String): Int {
    return try {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    } catch (e: Exception) {
        0
    }
}
