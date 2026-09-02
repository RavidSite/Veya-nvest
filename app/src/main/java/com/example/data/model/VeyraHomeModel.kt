package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Metadata and milestone structure for Veyra Home system.
 */
data class VeyraHomeTier(
    val level: Int,
    val name: String,
    val stageTitleAz: String,
    val minAmountAz: Double,
    val minAmountCents: Long,
    val statusAz: String,
    val shortDescAz: String,
    val detailedDescAz: String,
    val features: List<String>,
    val targetYieldPercent: Double,
    val durationDays: Int,
    val drawableResName: String,
    val badgeTitleAz: String,
    val primaryColorHex: Long = 0xFF10B981,
    val isElite: Boolean = false
)

object VeyraHomeConfig {
    val TIERS = listOf(
        VeyraHomeTier(
            level = 1,
            name = "Veyra Start",
            stageTitleAz = "Təməl Mərhələsi",
            minAmountAz = 25.0,
            minAmountCents = 2500L,
            statusAz = "Layihəyə başladınız",
            shortDescAz = "Başlanğıc səviyyə. Ev yalnız təməl və bünövrə mərhələsindədir.",
            detailedDescAz = "Veyra Home layihəsinin təməl daşı. Minimalist memarlıq layihələndirilməsi, bünövrə qazıntıları və ilkin infrastruktur hazırlığı.",
            features = listOf(
                "Bünövrə və monolit beton təməl",
                "İlkin memarlıq layihələndirilməsi",
                "İnvestor start sertifikatı",
                "Rüblük hesabat girişi"
            ),
            targetYieldPercent = 10.5,
            durationDays = 90,
            drawableResName = "img_home_foundation",
            badgeTitleAz = "Start Investor"
        ),
        VeyraHomeTier(
            level = 2,
            name = "Veyra Build",
            stageTitleAz = "Təməl + Divarlar",
            minAmountAz = 50.0,
            minAmountCents = 5000L,
            statusAz = "İnkişaf edir",
            shortDescAz = "Evdə ilk daşıyıcı divarlar ucalır və memarlıq konturları aydınlaşır.",
            detailedDescAz = "İnkişaf mərhələsi. Monolit sütunlar və daşıyıcı divarlar qaldırılır, tikinti sahəsi aktiv inkişaf fazasına daxil olur.",
            features = listOf(
                "İlk daşıyıcı divarlar və sütunlar",
                "Monolit dəmir-beton karkas",
                "İnkişaf edir statusu",
                "Aktiv monitorinq imkanı"
            ),
            targetYieldPercent = 12.0,
            durationDays = 180,
            drawableResName = "img_home_foundation",
            badgeTitleAz = "Builder"
        ),
        VeyraHomeTier(
            level = 3,
            name = "Veyra Growth",
            stageTitleAz = "Divarlar + Konstruksiya",
            minAmountAz = 100.0,
            minAmountCents = 10000L,
            statusAz = "Konstruksiya formalaşır",
            shortDescAz = "Əsas konstruksiya formalaşır, pəncərə və qapı elementləri əlavə olunur.",
            detailedDescAz = "Evin bütün mərtəbə örtükləri tamamlanır. Enerji qənaətli pəncərə və qapı profilləri üçün çərçivələr quraşdırılır.",
            features = listOf(
                "Əsas konstruksiya və mərtəbə örtüyü",
                "Pəncərə və qapı elementləri",
                "Mühəndislik və boru xətləri",
                "Growth Investor statusu"
            ),
            targetYieldPercent = 14.0,
            durationDays = 180,
            drawableResName = "img_home_foundation",
            badgeTitleAz = "Growth Investor"
        ),
        VeyraHomeTier(
            level = 4,
            name = "Veyra Residence",
            stageTitleAz = "Tam Konstruksiya & Bağ",
            minAmountAz = 250.0,
            minAmountCents = 25000L,
            statusAz = "Fasad və bağ sahəsi",
            shortDescAz = "Tam ev konstruksiyası, modern xarici fasad və şəxsi bağ sahəsi.",
            detailedDescAz = "Residence səviyyəsi. Evin fasad üzlənməsi, termoizolyasiya və yaşıl bağ sahəsinin ilkin salınması başa çatdırılır.",
            features = listOf(
                "Tam qapalı ev konstruksiyası",
                "Modern xarici fasad örtüyü",
                "Şəxsi dekorativ bağ sahəsi",
                "Ekoloji termoizolyasiya",
                "Residence Sahibi sertifikatı"
            ),
            targetYieldPercent = 16.5,
            durationDays = 365,
            drawableResName = "img_veyra_home_hero",
            badgeTitleAz = "Residence Owner"
        ),
        VeyraHomeTier(
            level = 5,
            name = "Veyra Premium",
            stageTitleAz = "Premium Fasad & Qaraj",
            minAmountAz = 500.0,
            minAmountCents = 50000L,
            statusAz = "Premium tərtibat",
            shortDescAz = "Premium fasad, dekorativ landşaft, işıqlandırma və qapalı qaraj.",
            detailedDescAz = "Premium memarlıq. Axşam LED memarlıq işıqlandırması, avtomatik jalüzlü qaraj və peşəkar landşaft dizaynı inteqrasiya olunur.",
            features = listOf(
                "Premium fasad və vitraj şüşələr",
                "Dekorativ landşaft və fəvvarə",
                "Axşam memarlıq işıqlandırması",
                "Qapalı avtomatlaşdırılmış qaraj",
                "Premium Investor imtiyazları"
            ),
            targetYieldPercent = 18.0,
            durationDays = 365,
            drawableResName = "img_veyra_home_hero",
            badgeTitleAz = "Premium Investor"
        ),
        VeyraHomeTier(
            level = 6,
            name = "Veyra Prestige",
            stageTitleAz = "Müasir Ev & Hovuz",
            minAmountAz = 750.0,
            minAmountCents = 75000L,
            statusAz = "Prestige səviyyə",
            shortDescAz = "Böyük və müasir ev, geniş bağ, premium hovuz və luxury interyer.",
            detailedDescAz = "Prestige standartı. Fərdi qızdırılan açıq hovuz, geniş meyvə və istirahət bağı, panoramik terras və xüsusi Prestige badge.",
            features = listOf(
                "Böyük və müasir malikanə",
                "Geniş şəxsi bağ və istirahət zonası",
                "Premium açıq üzgüçülük hovuzu",
                "Luxury interyer elementləri",
                "Prestige Member xüsusi nişanı"
            ),
            targetYieldPercent = 19.5,
            durationDays = 365,
            drawableResName = "img_veyra_home_hero",
            badgeTitleAz = "Prestige Member"
        ),
        VeyraHomeTier(
            level = 7,
            name = "Veyra Luxury",
            stageTitleAz = "Böyük Luxury Villa",
            minAmountAz = 1000.0,
            minAmountCents = 100000L,
            statusAz = "Luxury Investor",
            shortDescAz = "Böyük luxury villa, hovuz, qaraj, premium landşaft və luxury interyer.",
            detailedDescAz = "Eksklüziv Luxury Villa. İkimaşınlı qaraj, spa kompleksi, qızdırılan hovuz, premium floristik landşaft və Luxury Investor statusu.",
            features = listOf(
                "Böyük luxury villa kompleksi",
                "Qızdırılan infinity hovuz və terrasa",
                "İkimaşınlı qapalı qaraj",
                "Eksklüziv floristik landşaft",
                "Luxury interyer və smart-home",
                "Luxury Investor xüsusi statusu"
            ),
            targetYieldPercent = 21.0,
            durationDays = 730,
            drawableResName = "img_home_elite_villa",
            badgeTitleAz = "Luxury Investor"
        ),
        VeyraHomeTier(
            level = 8,
            name = "Veyra Elite",
            stageTitleAz = "Elite Villa Zirvəsi",
            minAmountAz = 1200.0,
            minAmountCents = 120000L,
            statusAz = "Elite Zirvəsi",
            shortDescAz = "Platformadakı ən yüksək səviyyə. Elite villa, nəhəng həyət, infinity hovuz, qaraj və Elite badge.",
            detailedDescAz = "Veyra Invest ekosisteminin ali zirvəsi. Ən yüksək memarlıq dəbdəbəsi, panoramik şüşəli elit villa, fərdi helipad/geniş park həyəti, infinity spa hovuzu, xüsusi işıqlandırma və Elite VIP statusu.",
            features = listOf(
                "Platformadakı ən ali zirvə səviyyəsi",
                "Elite villa və panoramik terras",
                "Nəhəng şəxsi həyət və landşaft parkı",
                "Infinity hovuz, spa və istirahət pavilyonu",
                "Premium axşam lazer və LED işıqlandırması",
                "Xüsusi Elite VIP badge və dashboard animasiyası"
            ),
            targetYieldPercent = 23.5,
            durationDays = 730,
            drawableResName = "img_home_elite_villa",
            badgeTitleAz = "Elite VIP Investor",
            primaryColorHex = 0xFFD97706,
            isElite = true
        )
    )

    fun calculateHomeStage(totalInvestedAz: Double): VeyraHomeCalculation {
        val currentTier = TIERS.lastOrNull { totalInvestedAz >= it.minAmountAz }
        val nextTier = TIERS.firstOrNull { it.minAmountAz > totalInvestedAz }

        val currentLevel = currentTier?.level ?: 0
        val currentName = currentTier?.name ?: "Veyra Start Başlanğıc"
        val currentStageTitle = currentTier?.stageTitleAz ?: "İlkin Təməl Mərhələsi"
        val currentStatus = currentTier?.statusAz ?: "İnvestisiya gözlənilir"
        val currentDrawable = currentTier?.drawableResName ?: "img_home_foundation"
        val badge = currentTier?.badgeTitleAz ?: "Gələcək Sahibkar"

        val minTargetAz = 25.0
        val maxCapAz = 1200.0

        val progressOverall = (totalInvestedAz / maxCapAz).toFloat().coerceIn(0f, 1f)

        val amountNeededForNext = if (nextTier != null) {
            (nextTier.minAmountAz - totalInvestedAz).coerceAtLeast(0.0)
        } else {
            0.0
        }

        val stepProgress = if (currentTier == null) {
            (totalInvestedAz / 25.0).toFloat().coerceIn(0f, 1f)
        } else if (nextTier == null) {
            1f
        } else {
            val range = nextTier.minAmountAz - currentTier.minAmountAz
            val earned = totalInvestedAz - currentTier.minAmountAz
            (earned / range).toFloat().coerceIn(0f, 1f)
        }

        return VeyraHomeCalculation(
            totalInvestedAz = totalInvestedAz,
            currentTier = currentTier,
            nextTier = nextTier,
            currentLevel = currentLevel,
            currentName = currentName,
            currentStageTitle = currentStageTitle,
            currentStatus = currentStatus,
            drawableResName = currentDrawable,
            badgeTitleAz = badge,
            progressOverall = progressOverall,
            stepProgress = stepProgress,
            amountNeededForNextAz = amountNeededForNext,
            isMaxTier = nextTier == null && currentTier?.level == 8,
            isElite = currentTier?.isElite == true
        )
    }
}

data class VeyraHomeCalculation(
    val totalInvestedAz: Double,
    val currentTier: VeyraHomeTier?,
    val nextTier: VeyraHomeTier?,
    val currentLevel: Int,
    val currentName: String,
    val currentStageTitle: String,
    val currentStatus: String,
    val drawableResName: String,
    val badgeTitleAz: String,
    val progressOverall: Float,
    val stepProgress: Float,
    val amountNeededForNextAz: Double,
    val isMaxTier: Boolean,
    val isElite: Boolean
)
