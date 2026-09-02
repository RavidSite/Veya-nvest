package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Premium Cinematic Startup / Opening Animation for Veyra Invest.
 * 
 * Stages:
 * 1. 0 - 250ms: Deep Pitch Black / Dark Navy silence.
 * 2. 250ms - 1200ms: Soft warm radiant golden halo bloom expanding from center.
 * 3. 800ms - 2200ms: Veyra 3D Gold Logo formation with metallic line stroke & rising skyscraper architecture.
 * 4. 1600ms - 3000ms: VEYRA (wide letter-spacing) + — INVEST — + "GƏLƏCƏYƏ DƏYƏR QATIRIQ" fade-in.
 * 5. 2800ms - 4000ms: Philosophy subtitle "Kiçik addımlar. Böyük gələcək." + faint villa silhouette.
 * 6. 3800ms - 4600ms: Radiant golden illumination flood light transition seamlessly into the app.
 */
@Composable
fun StartupSplashScreen(
    onAnimationComplete: () -> Unit
) {
    var animationStage by remember { mutableIntStateOf(0) }
    
    // Core animation triggers
    LaunchedEffect(Unit) {
        delay(200) // Initial dark silence
        animationStage = 1 // Gold light starts pulsing
        delay(600)
        animationStage = 2 // Logo emerges & draws
        delay(900)
        animationStage = 3 // VEYRA INVEST typography
        delay(900)
        animationStage = 4 // Tagline & philosophy
        delay(1200)
        animationStage = 5 // Golden light flood transition
        delay(700)
        onAnimationComplete()
    }

    // Shimmer sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Animated glow pulse
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // Logo scale & alpha transitions
    val logoAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 2) 1f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "logo_alpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = when {
            animationStage >= 5 -> 1.12f
            animationStage >= 2 -> 1f
            else -> 0.88f
        },
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 3) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "text_alpha"
    )

    val textOffsetY by animateFloatAsState(
        targetValue = if (animationStage >= 3) 0f else 25f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "text_offset"
    )

    val philosophyAlpha by animateFloatAsState(
        targetValue = if (animationStage in 4..4) 1f else if (animationStage >= 5) 0f else 0f,
        animationSpec = tween(600, easing = EaseInOutCubic),
        label = "philosophy_alpha"
    )

    val backdropAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 4) 0.15f else 0f,
        animationSpec = tween(1200, easing = EaseInOutCubic),
        label = "backdrop_alpha"
    )

    val transitionFlashAlpha by animateFloatAsState(
        targetValue = if (animationStage >= 5) 1f else 0f,
        animationSpec = tween(650, easing = EaseInCubic),
        label = "flash_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B11))
            .clickable { onAnimationComplete() } // Allow skip on tap
            .testTag("startup_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle luxury villa background appearing in later stage
        Image(
            painter = painterResource(id = R.drawable.img_villa_night_hero),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(backdropAlpha),
            contentScale = ContentScale.Crop
        )

        // Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC070B11),
                            Color(0xFF070B11)
                        ),
                        radius = 1200f
                    )
                )
        )

        // Stage 1+ Radial Golden Ambient Halo from center
        if (animationStage >= 1) {
            Canvas(
                modifier = Modifier
                    .size(340.dp)
                    .scale(if (animationStage >= 5) 2.5f else 1f)
                    .alpha(if (animationStage >= 1) 0.85f * glowPulse else 0f)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VeyraGoldPrimary.copy(alpha = 0.35f),
                            VeyraGoldPrimary.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width / 1.8f
                    )
                )
            }
        }

        // Center Content Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp)
        ) {
            // Emblem Container with Shimmer & Glow
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Outer Subtle Golden Ring
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Brush.sweepGradient(
                            listOf(
                                VeyraGoldLight,
                                VeyraGoldPrimary,
                                VeyraGoldDark,
                                VeyraGoldLight
                            )
                        )
                    ),
                    modifier = Modifier.size(136.dp)
                ) {}

                // 3D Gold Logo Image Asset
                Image(
                    painter = painterResource(id = R.drawable.img_veyra_gold_emblem),
                    contentDescription = "Veyra Gold Emblem",
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Metallic Sheen Light Sweep Canvas
                Canvas(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                ) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerOffset, 0f),
                            end = Offset(shimmerOffset + 120f, size.height)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // VEYRA Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffsetY.dp)
            ) {
                Text(
                    text = "V E Y R A",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        letterSpacing = 8.sp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFFAEB),
                                VeyraGoldLight,
                                VeyraGoldPrimary,
                                Color(0xFFC89945)
                            )
                        )
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // — INVEST —
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(1.dp)
                            .background(VeyraGoldPrimary.copy(alpha = 0.6f))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I N V E S T",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 6.sp,
                            color = VeyraGoldLight
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(1.dp)
                            .background(VeyraGoldPrimary.copy(alpha = 0.6f))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tagline: GƏLƏCƏYƏ DƏYƏR QATIRIQ
                Text(
                    text = "GƏLƏCƏYƏ DƏYƏR QATIRIQ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        letterSpacing = 3.sp,
                        color = VeyraGoldLight.copy(alpha = 0.85f)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Philosophy Banner
            Box(
                modifier = Modifier
                    .alpha(philosophyAlpha)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = VeyraNavyCard.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        VeyraGoldPrimary.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "Kiçik addımlar. Böyük gələcək.",
                        color = VeyraTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Skip / Quick continue indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(if (animationStage >= 2) 0.6f else 0f)
        ) {
            Text(
                text = "Toxunaraq keçin",
                color = VeyraTextSecondary.copy(alpha = 0.6f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }

        // Golden Flash Transition Layer
        if (animationStage >= 5) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(transitionFlashAlpha)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                VeyraGoldPrimary.copy(alpha = 0.9f),
                                Color(0xFF1E170A),
                                Color(0xFF070B11)
                            ),
                            radius = 1800f
                        )
                    )
            )
        }
    }
}
