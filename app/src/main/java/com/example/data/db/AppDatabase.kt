package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        InvestmentProductEntity::class,
        UserInvestmentEntity::class,
        TransactionEntity::class,
        DepositRequestEntity::class,
        WithdrawalRequestEntity::class,
        LedgerEntryEntity::class,
        PortfolioSnapshotEntity::class,
        AuditLogEntity::class,
        PaymentCardEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun veyraDao(): VeyraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "veyra_invest_secure.db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed database with initial real investment products & admin account
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).veyraDao()
                            seedInitialData(dao)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: VeyraDao) {
            // Seed Admin User
            val adminId = dao.insertUser(
                UserEntity(
                    fullName = "Veyra Baş Administrator",
                    phone = "+994501234567",
                    email = "admin@veyrainvest.az",
                    pinCode = "1234",
                    kycStatus = "Təsdiqləndi",
                    kycDocumentNo = "AA1234567",
                    kycFinCode = "7ABC123",
                    registeredAtMillis = System.currentTimeMillis() - 86400000L * 30,
                    isActive = true,
                    role = "ADMIN",
                    balanceCents = 0L
                )
            )

            // Seed Demo User
            val userId = dao.insertUser(
                UserEntity(
                    fullName = "Rəşad Əliyev",
                    phone = "+994509876543",
                    email = "rashad@example.com",
                    pinCode = "1111",
                    kycStatus = "Təsdiqləndi",
                    kycDocumentNo = "AZE98765432",
                    kycFinCode = "6XYZ890",
                    registeredAtMillis = System.currentTimeMillis() - 86400000L * 14,
                    isActive = true,
                    role = "USER",
                    balanceCents = 35000L // 350.00 AZN initial available balance
                )
            )

            // Seed Official Platform Payment Account (Birbank / Kapital Bank Card)
            dao.insertPaymentCard(
                PaymentCardEntity(
                    userId = 0L,
                    bankName = "Kapital Bank / Birbank",
                    cardNumber = "4169738849528363",
                    cardHolder = "VEYRA INVEST MMC / ÖDƏNİŞ HESABI",
                    expiryMonthYear = "12/28",
                    cvv = "836",
                    cardType = "VISA",
                    isOfficialPlatformAccount = true,
                    isDefault = true
                )
            )

            // Seed User Saved Card
            dao.insertPaymentCard(
                PaymentCardEntity(
                    userId = userId,
                    bankName = "Birbank Visa",
                    cardNumber = "4169738812345678",
                    cardHolder = "RASHAD ALIYEV",
                    expiryMonthYear = "08/27",
                    cvv = "452",
                    cardType = "VISA",
                    isOfficialPlatformAccount = false,
                    isDefault = false
                )
            )

            // Seed 8 official Veyra Home investment products requested:
            // 25 AZN, 50 AZN, 100 AZN, 250 AZN, 500 AZN, 750 AZN, 1000 AZN, 1200 AZN
            val products = listOf(
                InvestmentProductEntity(
                    titleAz = "Veyra Start",
                    categoryAz = "Səviyyə 1 • Təməl Mərhələsi",
                    minAmountCents = 2500L, // 25.00 AZN
                    maxAmountCents = 500000L,
                    annualYieldPercent = 10.50,
                    durationDays = 90,
                    riskLevelAz = "Aşağı",
                    commissionPercent = 0.20,
                    withdrawalTermsAz = "Müddət sonunda komissiyasız və ya 0.5% vaxtından əvvəl çıxarış",
                    descriptionAz = "Veyra Home başlanğıc səviyyəsi. Minimalist memarlıq layihəsi və bünövrənin qoyulması.",
                    drawableResName = "img_home_foundation"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Build",
                    categoryAz = "Səviyyə 2 • Təməl + Divarlar",
                    minAmountCents = 5000L, // 50.00 AZN
                    maxAmountCents = 1000000L,
                    annualYieldPercent = 12.00,
                    durationDays = 180,
                    riskLevelAz = "Aşağı",
                    commissionPercent = 0.25,
                    withdrawalTermsAz = "Aylıq faiz hesablanması və ya müddət sonunda kapitallaşma",
                    descriptionAz = "Evdə ilk daşıyıcı divarlar ucalır və strukturun memarlıq konturları aydınlaşır.",
                    drawableResName = "img_home_foundation"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Growth",
                    categoryAz = "Səviyyə 3 • Divarlar + Konstruksiya",
                    minAmountCents = 10000L, // 100.00 AZN
                    maxAmountCents = 2000000L,
                    annualYieldPercent = 14.00,
                    durationDays = 180,
                    riskLevelAz = "Orta",
                    commissionPercent = 0.30,
                    withdrawalTermsAz = "Rüblük gəlir bölgüsü, çevik portfel idarəetməsi",
                    descriptionAz = "Evin əsas konstruksiyası formalaşır, pəncərə və qapı elementləri quraşdırılır.",
                    drawableResName = "img_home_foundation"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Residence",
                    categoryAz = "Səviyyə 4 • Tam Konstruksiya & Bağ",
                    minAmountCents = 25000L, // 250.00 AZN
                    maxAmountCents = 5000000L,
                    annualYieldPercent = 16.50,
                    durationDays = 365,
                    riskLevelAz = "Orta",
                    commissionPercent = 0.40,
                    withdrawalTermsAz = "İllik kapitallaşma ilə tam sərbəst portfel balansı",
                    descriptionAz = "Tam ev konstruksiyası, modern xarici fasad işləmələri və ilkin bağ sahəsi.",
                    drawableResName = "img_veyra_home_hero"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Premium",
                    categoryAz = "Səviyyə 5 • Premium Fasad & Qaraj",
                    minAmountCents = 50000L, // 500.00 AZN
                    maxAmountCents = 10000000L,
                    annualYieldPercent = 18.00,
                    durationDays = 365,
                    riskLevelAz = "Orta",
                    commissionPercent = 0.45,
                    withdrawalTermsAz = "Aylıq icarə/gəlir payı hesaba köçürülür, 30 gün çıxarış bildirişi",
                    descriptionAz = "Premium fasad, dekorativ landşaft, axşam memarlıq işıqlandırması və qapalı qaraj.",
                    drawableResName = "img_veyra_home_hero"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Prestige",
                    categoryAz = "Səviyyə 6 • Müasir Ev & Hovuz",
                    minAmountCents = 75000L, // 750.00 AZN
                    maxAmountCents = 15000000L,
                    annualYieldPercent = 19.50,
                    durationDays = 365,
                    riskLevelAz = "Aşağı",
                    commissionPercent = 0.50,
                    withdrawalTermsAz = "Rüblük və ya illik mənfəət paylanması ilə yüksək likvidlik",
                    descriptionAz = "Böyük və müasir ev, geniş bağ, premium fərdi hovuz, luxury interyer və Prestige badge.",
                    drawableResName = "img_veyra_home_hero"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Luxury",
                    categoryAz = "Səviyyə 7 • Böyük Luxury Villa",
                    minAmountCents = 100000L, // 1 000.00 AZN
                    maxAmountCents = 25000000L,
                    annualYieldPercent = 21.00,
                    durationDays = 730,
                    riskLevelAz = "Orta",
                    commissionPercent = 0.55,
                    withdrawalTermsAz = "Yüksək likvidlik, bazar qiyməti ilə 3 iş günü ərzində çıxarış",
                    descriptionAz = "Böyük luxury villa, qızdırılan hovuz, çoxmaşınlı qaraj, xüsusi landşaft və Luxury Investor statusu.",
                    drawableResName = "img_home_elite_villa"
                ),
                InvestmentProductEntity(
                    titleAz = "Veyra Elite",
                    categoryAz = "Səviyyə 8 • Elite Villa Zirvəsi",
                    minAmountCents = 120000L, // 1 200.00 AZN
                    maxAmountCents = 50000000L,
                    annualYieldPercent = 23.50,
                    durationDays = 730,
                    riskLevelAz = "Yüksək",
                    commissionPercent = 0.60,
                    withdrawalTermsAz = "Müqavilə müddəti sonunda birbaşa mənfəət paylanması və VIP imtiyazlar",
                    descriptionAz = "Platformadakı ən yüksək səviyyə. Elite villa, böyük həyət, infinity hovuz, qaraj, premium işıqlandırma və Elite badge.",
                    drawableResName = "img_home_elite_villa"
                )
            )

            val pIds = mutableListOf<Long>()
            for (p in products) {
                pIds.add(dao.insertProduct(p))
            }

            // Seed user's initial real investment (e.g. 250 AZN invested in Veyra Residence -> level 4)
            val startTime = System.currentTimeMillis() - 86400000L * 35 // 35 days ago
            val investedCents = 25000L // 250.00 AZN
            val annualRate = 16.50
            val daysElapsed = 35
            val accruedProfitCents = (investedCents * (annualRate / 100.0) * (daysElapsed / 365.0)).toLong() // ~3.95 AZN
            val currentValuationCents = investedCents + accruedProfitCents

            val invId = dao.insertUserInvestment(
                UserInvestmentEntity(
                    userId = userId,
                    productId = pIds.getOrElse(3) { 1L },
                    productTitleAz = "Veyra Residence",
                    investedAmountCents = investedCents,
                    currentValuationCents = currentValuationCents,
                    accruedProfitCents = accruedProfitCents,
                    annualYieldPercent = annualRate,
                    startDateMillis = startTime,
                    maturityDateMillis = startTime + 86400000L * 365,
                    status = "Aktiv",
                    lastCalculatedAtMillis = System.currentTimeMillis(),
                    drawableResName = "img_veyra_home_hero"
                )
            )

            // Seed Ledger & Initial Transactions
            dao.insertTransaction(
                TransactionEntity(
                    transactionIdCode = "TXN-2026-88101",
                    userId = userId,
                    type = "DEPOZİT",
                    amountCents = 60000L, // 600.00 AZN
                    currency = "AZN",
                    feeCents = 0L,
                    status = "Tamamlandı",
                    paymentMethod = "Bank Kartı (Visa/Mastercard)",
                    timestampMillis = startTime - 3600000L,
                    notesAz = "Bank kartı ilə təhlükəsiz depozit ödənişi təsdiqləndi",
                    referenceId = "DEP-2026-001"
                )
            )

            dao.insertTransaction(
                TransactionEntity(
                    transactionIdCode = "TXN-2026-88102",
                    userId = userId,
                    type = "İNVESTİSİYA",
                    amountCents = 25000L,
                    currency = "AZN",
                    feeCents = 0L,
                    status = "Tamamlandı",
                    paymentMethod = "Balans",
                    timestampMillis = startTime,
                    notesAz = "Qlobal Texnologiya və İT İnnovasiyaları məhsuluna investisiya yatırıldı",
                    referenceId = "INV-$invId"
                )
            )

            dao.insertTransaction(
                TransactionEntity(
                    transactionIdCode = "TXN-2026-88103",
                    userId = userId,
                    type = "GƏLİR",
                    amountCents = accruedProfitCents,
                    currency = "AZN",
                    feeCents = 0L,
                    status = "Tamamlandı",
                    paymentMethod = "Gəlir Hesablanması",
                    timestampMillis = System.currentTimeMillis(),
                    notesAz = "Faktiki günlər üzrə hesablanmış real portfel gəliri",
                    referenceId = "YIELD-ACC"
                )
            )

            // Ledger Entries
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    entryCode = "LDG-00101",
                    transactionCode = "TXN-2026-88101",
                    debitAccount = "GATEWAY_RECEIVABLES",
                    creditAccount = "USER_WALLET",
                    amountCents = 60000L,
                    descriptionAz = "İstifadəçi balansına depozit daxil olması (Ödəniş təsdiqi əsasında)"
                )
            )
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    entryCode = "LDG-00102",
                    transactionCode = "TXN-2026-88102",
                    debitAccount = "USER_WALLET",
                    creditAccount = "ESCROW_INVESTMENT",
                    amountCents = 25000L,
                    descriptionAz = "İnvestisiya məhsulunun satın alınması və vəsaitin depozitariya depozitinə yönləndirilməsi"
                )
            )

            // Seed historical snapshots for portfolio charting (over 30 days)
            val now = System.currentTimeMillis()
            val snapshots = mutableListOf<PortfolioSnapshotEntity>()
            for (i in 30 downTo 0) {
                val t = now - (i * 86400000L)
                val dayProfit = if (i > 35) 0L else {
                    val d = 35 - i
                    (investedCents * (annualRate / 100.0) * (d / 365.0)).toLong()
                }
                snapshots.add(
                    PortfolioSnapshotEntity(
                        userId = userId,
                        timestampMillis = t,
                        totalBalanceCents = 35000L + investedCents + dayProfit,
                        investedCents = investedCents,
                        profitLossCents = dayProfit,
                        intervalTag = "30D"
                    )
                )
            }
            dao.insertPortfolioSnapshots(snapshots)

            // Seed initial Audit Log
            dao.insertAuditLog(
                AuditLogEntity(
                    adminEmail = "system@veyrainvest.az",
                    actionType = "SİSTEM_BAŞLATMA",
                    targetType = "MƏHSULLAR",
                    targetId = "ALL",
                    detailsAz = "Veyra Invest standart 8 investisiya məhsulu və maliyyə ledger sistemi başladıldı"
                )
            )
        }
    }
}
