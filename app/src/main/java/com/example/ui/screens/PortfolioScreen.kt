package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserInvestmentEntity
import com.example.data.model.VeyraHomeConfig
import com.example.ui.components.UserInvestmentCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PortfolioScreen(
    user: UserEntity?,
    investments: List<UserInvestmentEntity>,
    onExploreClick: () -> Unit
) {
    val totalInvestedAz = investments.filter { it.status == "Aktiv" }.sumOf { it.investedAmountCents } / 100.0
    val totalAccruedProfitAz = investments.filter { it.status == "Aktiv" }.sumOf { it.accruedProfitCents } / 100.0
    val totalValuationAz = totalInvestedAz + totalAccruedProfitAz

    // Real mathematical yield projections based on actual current holdings
    val avgYieldPercent = if (investments.isNotEmpty()) {
        investments.sumOf { it.investedAmountCents * it.annualYieldPercent } / investments.sumOf { it.investedAmountCents }.toDouble()
    } else 0.0

    val dailyEstAz = (totalInvestedAz * (avgYieldPercent / 100.0)) / 365.0
    val monthlyEstAz = dailyEstAz * 30.0
    val annualEstAz = totalInvestedAz * (avgYieldPercent / 100.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("portfolio_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "İnvestisiya Portfeliniz",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                Text(
                    text = "Dəqiq mühasibat uçotu və real vaxt gəlir hesabatı",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary
                    )
                )
            }
        }

        // Veyra Home Status Banner
        item {
            val homeCalc = VeyraHomeConfig.calculateHomeStage(totalInvestedAz)
            Surface(
                color = if (homeCalc.isElite) Color(0xFF281C09) else Color(0xFF132824),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (homeCalc.isElite) VeyraGold.copy(alpha = 0.5f) else VeyraEmerald.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (homeCalc.isElite) VeyraGold.copy(alpha = 0.2f) else VeyraEmerald.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (homeCalc.isElite) Icons.Default.AutoAwesome else Icons.Default.HomeWork,
                                contentDescription = null,
                                tint = if (homeCalc.isElite) VeyraGold else VeyraEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Veyra Home: ${homeCalc.currentName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (homeCalc.isElite) VeyraGoldLight else VeyraTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0x99000000),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Səviyyə ${homeCalc.currentLevel}/8",
                                    fontSize = 10.sp,
                                    color = if (homeCalc.isElite) VeyraGold else VeyraEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${homeCalc.currentStageTitle} • ${String.format(Locale.US, "%.0f", totalInvestedAz)} / 1,200 AZN",
                            fontSize = 11.sp,
                            color = VeyraTextSecondary
                        )
                    }
                }
            }
        }

        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Cari Portfel Dəyəri", fontSize = 12.sp, color = VeyraTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format(Locale.US, "%.2f AZN", totalValuationAz),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = VeyraTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VeyraNavyElevated, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Əsas Maya Vəsaiti", fontSize = 11.sp, color = VeyraTextMuted)
                            Text(
                                String.format(Locale.US, "%.2f AZN", totalInvestedAz),
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary,
                                fontSize = 13.sp
                            )
                        }
                        Column {
                            Text("Qazanılmış Real Gəlir", fontSize = 11.sp, color = VeyraTextMuted)
                            Text(
                                String.format(Locale.US, "+%.2f AZN", totalAccruedProfitAz),
                                fontWeight = FontWeight.Bold,
                                color = VeyraEmerald,
                                fontSize = 13.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Orta İllik Faiz", fontSize = 11.sp, color = VeyraTextMuted)
                            Text(
                                String.format(Locale.US, "%.1f%%", avgYieldPercent),
                                fontWeight = FontWeight.Bold,
                                color = VeyraGold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 5: Real Return Calculations & Projection Breakdown
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
                border = BorderStroke(1.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = VeyraEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gəlir Hesablama Mexanizmi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = VeyraTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gəlirlər saxta animasiya və ya təsadüfi ədədlər deyil, investisiyanızın hər bir gününə düşən dəqiq riyazi faiz dərəcəsi ilə hesablanır.",
                        fontSize = 11.sp,
                        color = VeyraTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReturnEstimateBox(
                            title = "Gündəlik Gəlir",
                            amount = String.format(Locale.US, "+%.2f AZN", dailyEstAz),
                            modifier = Modifier.weight(1f)
                        )
                        ReturnEstimateBox(
                            title = "Aylıq Gəlir",
                            amount = String.format(Locale.US, "+%.2f AZN", monthlyEstAz),
                            modifier = Modifier.weight(1f)
                        )
                        ReturnEstimateBox(
                            title = "İllik Gəlir",
                            amount = String.format(Locale.US, "+%.2f AZN", annualEstAz),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Active Investments List
        item {
            Text(
                text = "İnvestisiya Paketləri (${investments.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = VeyraTextPrimary
                )
            )
        }

        if (investments.isEmpty()) {
            item {
                Surface(
                    color = VeyraNavyCard,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Portfelinizdə hələ aktiv məhsul yoxdur.",
                            color = VeyraTextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(containerColor = VeyraEmerald, contentColor = Color(0xFF042017))
                        ) {
                            Text("İnvestisiya Et", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(investments, key = { it.id }) { inv ->
                UserInvestmentCard(
                    investment = inv,
                    onDetailsClick = {}
                )
            }
        }
    }
}

@Composable
private fun ReturnEstimateBox(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = VeyraNavyElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, VeyraNavyBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = VeyraTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VeyraEmerald)
        }
    }
}
