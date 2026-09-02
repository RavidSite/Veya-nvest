package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.InvestmentProductEntity
import com.example.ui.theme.*

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val highlightTag: String
)

@Composable
fun LandingScreen(
    featuredProducts: List<InvestmentProductEntity>,
    onExploreInvestments: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onProductClick: (InvestmentProductEntity) -> Unit,
    onLegalClick: () -> Unit
) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                title = "Veyra Home ilə\ninvestisiya edin.",
                subtitle = "Kiçik addımlarla başlayın,\nböyük gələcəyə sahib olun.",
                highlightTag = "25 AZN-dən Başlayaraq"
            ),
            OnboardingSlide(
                title = "Ağ Şəhər & Premium\nRezidensiyalar",
                subtitle = "Bakının ən prestijli daşınmaz əmlak\nlayihələrindən pay sahibi olun.",
                highlightTag = "İllik 14.5% - 18.2% Gəlir"
            ),
            OnboardingSlide(
                title = "Dövlət Zəmanətli\nİstiqraz və Qızıl",
                subtitle = "Tam sığortalanmış, likvid və\nşəffaf aktivlərlə maliyyə azadlığı.",
                highlightTag = "100% Təhlükəsiz Portfel"
            ),
            OnboardingSlide(
                title = "Gündəlik Qazanc &\nŞəffaf Mühasibatlıq",
                subtitle = "Mənfəətinizi canlı izləyin və\nistənilən an kartınıza çıxarın.",
                highlightTag = "0% Komissiyasız Çıxarış"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B11))
            .testTag("landing_screen")
    ) {
        // Full screen luxury villa night background
        Image(
            painter = painterResource(id = R.drawable.img_villa_night_hero),
            contentDescription = "Luxury Villa Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient vignette overlays for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEE070B11),
                            Color(0x99070B11),
                            Color(0xCC070B11),
                            Color(0xFF070B11)
                        ),
                        startY = 0f,
                        endY = 2200f
                    )
                )
        )

        // Main Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP: Logo Branding & Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                // 3D Golden Emblem
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            Brush.sweepGradient(
                                listOf(
                                    VeyraGoldLight,
                                    VeyraGoldPrimary,
                                    VeyraGoldDark,
                                    VeyraGoldLight
                                )
                            )
                        ),
                        modifier = Modifier.size(88.dp)
                    ) {}

                    Image(
                        painter = painterResource(id = R.drawable.img_veyra_gold_emblem),
                        contentDescription = "Veyra Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VEYRA Brand Text
                Text(
                    text = "V E Y R A",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 6.sp,
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

                Spacer(modifier = Modifier.height(4.dp))

                // — INVEST —
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(1.dp)
                            .background(VeyraGoldPrimary.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I N V E S T",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 4.sp,
                            color = VeyraGoldLight
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(1.dp)
                            .background(VeyraGoldPrimary.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tagline: GƏLƏCƏYƏ DƏYƏR QATIRIQ
                Text(
                    text = "GƏLƏCƏYƏ DƏYƏR QATIRIQ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        color = VeyraGoldLight.copy(alpha = 0.85f)
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5 GOLDEN FEATURE CHIPS (Exactly as in user's mockup)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x990A101D)),
                border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("feature_badges_container")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadgeItem(
                        icon = Icons.Outlined.Shield,
                        label = "TƏHLÜKƏSİZ"
                    )
                    FeatureBadgeItem(
                        icon = Icons.Outlined.TrendingUp,
                        label = "GƏLİRLİ"
                    )
                    FeatureBadgeItem(
                        icon = Icons.Outlined.Home,
                        label = "DAŞINMAZ\nƏMLAK"
                    )
                    FeatureBadgeItem(
                        icon = Icons.Outlined.PieChart,
                        label = "DİVERSİFİ-\nKASİYA"
                    )
                    FeatureBadgeItem(
                        icon = Icons.Outlined.Handshake,
                        label = "ETİBARLI"
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SWIPEABLE ONBOARDING PAGER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) { page ->
                    val slide = slides[page]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = slide.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = VeyraTextPrimary,
                                fontSize = 21.sp,
                                lineHeight = 27.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = slide.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = VeyraTextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Pager Indicators (Dots)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .testTag("pager_indicator")
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isSelected) 22.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isSelected) VeyraGoldPrimary
                                    else VeyraTextSecondary.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ACTION BUTTONS SECTION
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Gold Button: "Başla →"
                Button(
                    onClick = onExploreInvestments,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFE5C07B),
                                    VeyraGoldPrimary,
                                    Color(0xFFC69234)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .testTag("landing_start_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Başla",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
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

                // Secondary Button: "Daxil ol"
                OutlinedButton(
                    onClick = onLoginClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraNavyBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x990A101D)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("landing_login_button")
                ) {
                    Text(
                        text = "Daxil ol",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VeyraTextPrimary
                    )
                }

                // Real Google Sign-in Button
                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x66151E2E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("landing_google_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google "G" representation
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    color = Color(0xFF4285F4),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Google ilə davam et",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = VeyraTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureBadgeItem(
    icon: ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VeyraGoldPrimary.copy(alpha = 0.12f))
                .border(1.dp, VeyraGoldPrimary.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VeyraGoldLight,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = VeyraGoldLight.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
    }
}
