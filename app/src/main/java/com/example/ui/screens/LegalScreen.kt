package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LegalScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VeyraNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("legal_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Hüquqi Məlumat və Şəffaflıq",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = VeyraTextPrimary
                    )
                )
                Text(
                    text = "Veyra Invest platformasının fəaliyyət prinsipləri və təhlükəsizlik qaydaları",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = VeyraTextSecondary
                    )
                )
            }
        }

        // Section 17 Items
        item {
            LegalSectionCard(
                icon = Icons.Outlined.Gavel,
                title = "1. İstifadə Şərtləri",
                content = "Veyra Invest platformasından istifadə edən hər bir şəxs Azərbaycan Respublikasının mülki və maliyyə qanunvericiliyinə tabedir. Platformada qeydiyyatdan keçən istifadəçi təqdim etdiyi məlumatların doğruluğuna və hesab təhlükəsizliyinə cavabdehdir."
            )
        }

        item {
            LegalSectionCard(
                icon = Icons.Outlined.Lock,
                title = "2. Məxfilik və Məlumatların Qorunması",
                content = "İstifadəçilərin şəxsi və maliyyə məlumatları 256-bit SSL şifrələmə ilə qorunur. Bank kartı məlumatları (PAN, CVV) heç bir halda Veyra Invest bazasında saxlanılmır, beynəlxalq PCI-DSS sertifikatlı təhlükəsiz tokenizasiya sistemi ilə emal olunur."
            )
        }

        item {
            LegalSectionCard(
                icon = Icons.Outlined.Warning,
                title = "3. İnvestisiya Riskləri Bildirişi",
                content = "Bütün maliyyə və investisiya alətləri bazar konyunkturasından asılı olaraq risk daşıyır. Keçmiş gəlirlilik gələcək nəticələrə 100% zəmanət vermir. İstifadəçilərə vəsaitlərini müxtəlif aktivlər (istiqrazlar, əmlak, yaşıl enerji) üzrə diversifikasiya etmək tövsiyə olunur."
            )
        }

        item {
            LegalSectionCard(
                icon = Icons.Outlined.Percent,
                title = "4. Komissiyalar və Xidmət Haqları",
                content = "Platformada gizli komissiyalar yoxdur. Depozit əməliyyatları: 0% komissiya. Çıxarış əməliyyatları: 0.5% (min. 0.50 AZN). Aktivlərin idarəetmə xidmət haqqı məhsulun şərtlərinə uyğun olaraq 0.2% - 0.5% arasında tətbiq edilir."
            )
        }

        item {
            LegalSectionCard(
                icon = Icons.Outlined.AccountBalance,
                title = "5. Çıxarış və AML / KYC Qaydaları",
                content = "Çirkli pulların yuyulmasına qarşı (AML) və müştərini tanı (KYC) tələblərinə əsasən, çıxarış yalnız istifadəçinin öz adına olan təsdiqlənmiş bank hesabına və ya kartına icra olunur. Çıxarış sorğuları maliyyə şöbəsi tərəfindən 1-24 saat ərzində təsdiqlənir."
            )
        }

        item {
            LegalSectionCard(
                icon = Icons.Outlined.ContactSupport,
                title = "6. Əlaqə və Rəsmi Məlumat",
                content = "Ünvan: Nizami küçəsi 142, Landmark III, Bakı, Azərbaycan\nE-poçt: support@veyrainvest.az\nQaynar xətt: +994 (12) 490 00 00 / *0808\nİş saatları: Bazar ertəsi - Cümə, 09:00 - 18:00"
            )
        }
    }
}

@Composable
private fun LegalSectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VeyraNavyCard),
        border = BorderStroke(1.dp, VeyraNavyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VeyraEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = VeyraEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = VeyraTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                fontSize = 12.sp,
                color = VeyraTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
