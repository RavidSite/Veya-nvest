package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.VeyraScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeyraTopBar(
    currentScreen: VeyraScreen,
    user: UserEntity?,
    onNavigate: (VeyraScreen) -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    if (user != null) onNavigate(VeyraScreen.DASHBOARD) else onNavigate(VeyraScreen.LANDING)
                }
            ) {
                // Gold Emblem with circular border
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0C121D))
                        .border(
                            1.dp,
                            Brush.sweepGradient(
                                listOf(
                                    VeyraGoldLight,
                                    VeyraGoldPrimary,
                                    VeyraGoldDark,
                                    VeyraGoldLight
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_veyra_gold_emblem),
                        contentDescription = "Veyra Invest Loqo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "V E Y R A",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        letterSpacing = 2.sp,
                        color = VeyraTextPrimary
                    )
                    Text(
                        text = "I N V E S T",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        color = VeyraGoldLight
                    )
                }
            }
        },
        actions = {
            if (user != null) {
                // KYC Pill
                Surface(
                    color = if (user.kycStatus == "Təsdiqləndi") Color(0x3310B981) else Color(0x33D97706),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (user.kycStatus == "Təsdiqləndi") VeyraEmerald.copy(alpha = 0.4f) else VeyraGold.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = if (user.kycStatus == "Təsdiqləndi") "KYC ✓" else "KYC !",
                        color = if (user.kycStatus == "Təsdiqləndi") VeyraEmerald else VeyraGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Logout icon
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Çıxış",
                        tint = VeyraTextSecondary
                    )
                }
            } else {
                TextButton(
                    onClick = { onNavigate(VeyraScreen.AUTH) },
                    modifier = Modifier.testTag("topbar_login_button")
                ) {
                    Text("Daxil ol", color = VeyraGoldLight, fontWeight = FontWeight.Bold)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF070B11),
            titleContentColor = VeyraTextPrimary
        ),
        windowInsets = WindowInsets.statusBars
    )
}

data class BottomNavItem(
    val screen: VeyraScreen,
    val title: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

@Composable
fun VeyraBottomBar(
    currentScreen: VeyraScreen,
    onNavigate: (VeyraScreen) -> Unit
) {
    val items = listOf(
        BottomNavItem(VeyraScreen.DASHBOARD, "Ana səhifə", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(VeyraScreen.INVESTMENTS, "İnvestisiyalar", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
        BottomNavItem(VeyraScreen.PORTFOLIO, "Portfel", Icons.Filled.PieChart, Icons.Outlined.PieChartOutline),
        BottomNavItem(VeyraScreen.TRANSACTIONS, "Əməliyyatlar", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
        BottomNavItem(VeyraScreen.LEGAL, "Haqqımızda", Icons.Filled.Info, Icons.Outlined.Info)
    )

    Surface(
        color = Color(0xFF070B11),
        border = BorderStroke(1.dp, VeyraGoldPrimary.copy(alpha = 0.2f))
    ) {
        NavigationBar(
            containerColor = Color(0xFF070B11),
            contentColor = VeyraTextPrimary,
            tonalElevation = 0.dp,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("veyra_bottom_nav")
        ) {
            items.forEach { item ->
                val isSelected = currentScreen == item.screen
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.screen) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF141006),
                        selectedTextColor = VeyraGoldLight,
                        indicatorColor = VeyraGoldPrimary,
                        unselectedIconColor = VeyraTextSecondary.copy(alpha = 0.6f),
                        unselectedTextColor = VeyraTextSecondary.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

