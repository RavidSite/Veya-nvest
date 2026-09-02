package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvestmentProductEntity
import com.example.data.model.VeyraHomeCalculation
import com.example.data.model.VeyraHomeConfig
import com.example.data.model.VeyraHomeTier
import com.example.ui.theme.*
import java.util.Locale

/**
 * Main Interactive Veyra Home Hero Card for the Dashboard.
 * Displays user's actual cumulative home stage, visual illustration,
 * multi-tier milestone progress stepper, and next upgrade CTA.
 */
@Composable
fun VeyraHomeHeroCard(
    homeCalc: VeyraHomeCalculation,
    onUpgradeClick: (targetTier: VeyraHomeTier?, suggestedAmountAz: Double) -> Unit,
    onExploreAllTiersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTier = homeCalc.currentTier
    val nextTier = homeCalc.nextTier
    val isElite = homeCalc.isElite

    // Pulsing animation for active indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val cardBorderBrush = Brush.linearGradient(
        listOf(
            VeyraGoldDark,
            VeyraGoldPrimary,
            VeyraGoldLight,
            VeyraGoldPrimary
        )
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
        border = BorderStroke(1.2.dp, cardBorderBrush),
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = VeyraGoldPrimary.copy(alpha = 0.25f))
            .testTag("veyra_home_hero_card")
    ) {
        Column(
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
            // Header Row: Logo & Stage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isElite) VeyraGold.copy(alpha = 0.2f) else VeyraEmerald.copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isElite) Icons.Default.AutoAwesome else Icons.Default.HomeWork,
                                contentDescription = null,
                                tint = if (isElite) VeyraGold else VeyraEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Veyra Home",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary,
                                letterSpacing = 0.2.sp
                            )
                        )
                        Text(
                            text = if (currentTier != null) "Səviyyə ${currentTier.level} / 8 • ${currentTier.name}" else "Başlanğıc Səviyyə (0 AZN)",
                            fontSize = 11.sp,
                            color = if (isElite) VeyraGold else VeyraEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Stage Status Badge
                Surface(
                    color = if (isElite) Color(0xFF451A03) else Color(0xFF133E35),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isElite) VeyraGold.copy(alpha = 0.5f) else VeyraEmerald.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isElite) VeyraGold.copy(alpha = pulseAlpha) else VeyraEmerald.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = homeCalc.currentStatus,
                            color = if (isElite) Color(0xFFFDE68A) else Color(0xFF6EE7B7),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual House Presentation Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A1513))
            ) {
                val imgResId = rememberDrawableId(context, homeCalc.drawableResName)
                if (imgResId != 0) {
                    Image(
                        painter = painterResource(id = imgResId),
                        contentDescription = "Veyra Home Visual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Subtle gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x990F1E1B),
                                    Color(0xF00F1E1B)
                                )
                            )
                        )
                )

                // Floating House Level & Title Info Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xCC0F1E1B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.8.dp, VeyraNavyBorder)
                        ) {
                            Text(
                                text = "🏠 ${homeCalc.currentStageTitle}",
                                color = VeyraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (isElite) {
                            Surface(
                                color = Color(0xCC78350F),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.8.dp, VeyraGold)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = VeyraGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Elite VIP",
                                        color = VeyraGoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Eviniz hazırda ${String.format(Locale.US, "%.0f", homeCalc.totalInvestedAz)} AZN səviyyəsindədir.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                }

                // Amount Tag
                Surface(
                    color = Color(0xEE141D2D),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, VeyraGoldBorder),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.0f", homeCalc.totalInvestedAz)} / 1,200 AZN",
                        color = if (isElite) VeyraGold else VeyraEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Smooth Animated Continuous Progress Bar (0% -> target%)
            val targetProgress = (homeCalc.totalInvestedAz / 1200.0).coerceIn(0.0, 1.0).toFloat()
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(1200, easing = FastOutSlowInEasing),
                label = "home_progress"
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Veyra Home İnkişaf Göstəricisi",
                        fontSize = 11.sp,
                        color = VeyraTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", animatedProgress * 100)}%",
                        fontSize = 12.sp,
                        color = if (isElite) VeyraGoldLight else VeyraEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(VeyraNavyElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(VeyraGoldDark, VeyraGoldPrimary, VeyraGoldLight)
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Stage Progress Stepper
            Text(
                text = "İnvestisiya və İnkişaf Mərhələləri",
                fontSize = 12.sp,
                color = VeyraTextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            VeyraHomeMultiStageStepper(
                totalInvestedAz = homeCalc.totalInvestedAz,
                onMilestoneClick = { tier ->
                    val diff = (tier.minAmountAz - homeCalc.totalInvestedAz).coerceAtLeast(0.0)
                    onUpgradeClick(tier, diff)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Upgrade Recommendation Box
            Surface(
                color = if (isElite) Color(0xFF281C09) else VeyraNavyElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isElite) VeyraGold.copy(alpha = 0.3f) else VeyraNavyBorder),
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
                        if (nextTier != null) {
                            Text(
                                text = "Növbəti Mərhələ: ${nextTier.name}",
                                fontSize = 11.sp,
                                color = VeyraTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Evinizi yüksəltmək üçün ${String.format(Locale.US, "%.0f", homeCalc.amountNeededForNextAz)} AZN əlavə edin.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary
                            )
                        } else {
                            Text(
                                text = "✨ Təbriklər! Siz Elite Zirvəsindəsiniz",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VeyraGold
                            )
                            Text(
                                text = "Veyra Home layihəsinin ən yüksək memarlıq və investisiya mərhələsi tamamlandı.",
                                fontSize = 11.sp,
                                color = VeyraTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (nextTier != null) {
                        Button(
                            onClick = {
                                onUpgradeClick(nextTier, homeCalc.amountNeededForNextAz)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VeyraGoldPrimary,
                                contentColor = Color(0xFF141006)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("upgrade_home_button")
                        ) {
                            Text("Yüksəlt", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onExploreAllTiersClick,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, VeyraGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VeyraGold),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Məhsullar", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual multi-tier progress stepper bar showing:
 * 25 AZN ━━━ 50 AZN ━━━ 100 AZN ━━━ 250 AZN ━━━ 500 AZN ━━━ 750 AZN ━━━ 1000 AZN ━━━ 1200 AZN
 */
@Composable
fun VeyraHomeMultiStageStepper(
    totalInvestedAz: Double,
    onMilestoneClick: (VeyraHomeTier) -> Unit,
    modifier: Modifier = Modifier
) {
    val tiers = VeyraHomeConfig.TIERS

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F1E1B), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        // Horizontal Scrollable Stepper Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tiers.forEachIndexed { index, tier ->
                val isReached = totalInvestedAz >= tier.minAmountAz
                val isCurrent = isReached && (index == tiers.size - 1 || totalInvestedAz < tiers[index + 1].minAmountAz)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMilestoneClick(tier) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    // Milestone Circle / Pill
                    Surface(
                        color = when {
                            isCurrent -> if (tier.isElite) VeyraGold else VeyraEmerald
                            isReached -> if (tier.isElite) Color(0xFF78350F) else Color(0xFF133E35)
                            else -> VeyraNavyElevated
                        },
                        shape = CircleShape,
                        border = BorderStroke(
                            1.dp,
                            when {
                                isCurrent -> Color.White
                                isReached -> if (tier.isElite) VeyraGold else VeyraEmerald
                                else -> VeyraNavyBorder
                            }
                        ),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isReached) {
                                Icon(
                                    imageVector = if (tier.isElite) Icons.Default.Star else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isCurrent) Color(0xFF06201B) else if (tier.isElite) VeyraGoldLight else VeyraEmeraldLight,
                                    modifier = Modifier.size(12.dp)
                                )
                            } else {
                                Text(
                                    text = "${tier.level}",
                                    fontSize = 10.sp,
                                    color = VeyraTextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${tier.minAmountAz.toInt()} ₼",
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) (if (tier.isElite) VeyraGold else VeyraEmerald) else if (isReached) VeyraTextPrimary else VeyraTextMuted
                    )
                }

                // Connecting Line between steps
                if (index < tiers.size - 1) {
                    val nextReached = totalInvestedAz >= tiers[index + 1].minAmountAz
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(2.dp)
                            .background(
                                if (nextReached) VeyraEmerald else VeyraNavyBorder
                            )
                    )
                }
            }
        }
    }
}

/**
 * Large Luxury Product Card for "İnvestisiya et" Screen.
 * Showcases the specific Veyra Home stage with progressive luxury styling,
 * architecture illustration, features, yield, and "Başla" action.
 */
@Composable
fun VeyraHomeProductCard(
    tier: VeyraHomeTier,
    product: InvestmentProductEntity?,
    isCurrentActiveStage: Boolean,
    onInvestClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isElite = tier.isElite
    val imgResId = rememberDrawableId(context, tier.drawableResName)

    val borderStroke = when {
        isElite || isCurrentActiveStage -> BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(VeyraGoldLight, VeyraGoldPrimary, VeyraGoldDark)
            )
        )
        else -> BorderStroke(1.dp, VeyraGoldBorder)
    }

    val cardBgGradient = Brush.verticalGradient(listOf(Color(0xFF141D2E), Color(0xFF0B101B)))

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = borderStroke,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
            .testTag("product_card_${tier.name.lowercase(Locale.ROOT).replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBgGradient)
                .padding(18.dp)
        ) {
            // Visual Image Header with stage tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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

                // Gradient wash
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x33000000), Color(0x990F1E1B), Color(0xEE0F1E1B))
                            )
                        )
                )

                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xDD0F1E1B),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, VeyraNavyBorder)
                    ) {
                        Text(
                            text = "Səviyyə ${tier.level} • ${tier.stageTitleAz}",
                            color = if (isElite) VeyraGold else VeyraEmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isElite) {
                        Surface(
                            color = Color(0xEE78350F),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, VeyraGold)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = VeyraGoldLight, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Elite VIP",
                                    color = VeyraGoldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (isCurrentActiveStage) {
                        Surface(
                            color = Color(0xEE1E2738),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, VeyraGoldPrimary)
                        ) {
                            Text(
                                text = "Cari Mərhələniz",
                                color = VeyraGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Bottom Status
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xCC0F1E1B),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Status: ${tier.statusAz}",
                            color = VeyraTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tier.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isElite) VeyraGoldLight else VeyraTextPrimary
                        )
                    )
                    Text(
                        text = tier.shortDescAz,
                        fontSize = 12.sp,
                        color = VeyraTextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Financial & House Metrics Grid
            Surface(
                color = VeyraNavyElevated.copy(alpha = 0.8f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(0.8.dp, VeyraNavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Min. İnvestisiya", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            text = "${tier.minAmountAz.toInt()} AZN",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isElite) VeyraGold else VeyraEmerald
                        )
                    }

                    Column {
                        Text("Hədəflənən Gəlir", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            text = "+${tier.targetYieldPercent}% illik",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VeyraEmeraldLight
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Müddət", fontSize = 11.sp, color = VeyraTextMuted)
                        Text(
                            text = "${tier.durationDays} gün",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VeyraTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Features Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                tier.features.take(3).forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isElite) VeyraGold else VeyraEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feat,
                            fontSize = 11.sp,
                            color = VeyraTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            Button(
                onClick = onInvestClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isElite) VeyraGold else VeyraEmerald,
                    contentColor = if (isElite) Color(0xFF281800) else Color(0xFF042017)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = if (isElite) Icons.Default.AutoAwesome else Icons.Default.AddHome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Başla • ${tier.minAmountAz.toInt()} AZN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Premium Celebration Dialog for Approved Deposit & Veyra Home Stage Upgrades.
 */
@Composable
fun DepositCelebrationDialog(
    amountAz: Double,
    newBalanceAz: Double,
    homeStageTitle: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VeyraEmerald,
                    contentColor = Color(0xFF042017)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("celebration_continue_button")
            ) {
                Text("Davam et", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        shape = RoundedCornerShape(26.dp),
        containerColor = VeyraNavyCard,
        title = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Success Animated Icon Circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    VeyraEmerald,
                                    VeyraGold,
                                    VeyraEmerald
                                )
                            )
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0C1A17)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Təsdiqləndi",
                        tint = VeyraEmerald,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Depozit Təsdiqləndi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "+${String.format(Locale.US, "%.2f", amountAz)} AZN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = VeyraEmerald
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Yeni balans: ${String.format(Locale.US, "%.2f", newBalanceAz)} AZN",
                    fontSize = 13.sp,
                    color = VeyraTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Home Progression Message Box
                Surface(
                    color = VeyraNavyElevated,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏠 Veyra Home İrəliləyişi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VeyraGoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Təbrik edirik! $homeStageTitle mərhələsində aktiv iştirakınız möhkəmləndi.",
                            fontSize = 12.sp,
                            color = VeyraTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

