package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User Entity for Veyra Invest platform.
 * Supports KYC state, authentication details, and user role.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val pinCode: String = "",
    val kycStatus: String = "Yoxlanılmayıb", // Yoxlanılmayıb, Gözləmədə, Təsdiqləndi, Rədd edildi
    val kycDocumentNo: String = "",
    val kycFinCode: String = "",
    val registeredAtMillis: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val role: String = "USER", // USER, ADMIN
    val balanceCents: Long = 0L // Stored in qəpik (e.g., 10000 = 100.00 AZN) to ensure financial precision
)

/**
 * Investment Product Entity.
 * Admin can manage yield, minimum amount, risk levels, duration, and commission.
 */
@Entity(tableName = "investment_products")
data class InvestmentProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleAz: String,
    val categoryAz: String,
    val minAmountCents: Long, // e.g. 2000L = 20.00 AZN
    val maxAmountCents: Long = 10000000L,
    val annualYieldPercent: Double, // e.g. 14.5%
    val durationDays: Int, // e.g. 90, 180, 365
    val riskLevelAz: String, // "Aşağı", "Orta", "Yüksək"
    val commissionPercent: Double = 0.5, // e.g. 0.5%
    val withdrawalTermsAz: String, // "Müddət sonunda komissiyasız və ya 1.0% vaxtından əvvəl çıxarış"
    val descriptionAz: String,
    val drawableResName: String, // "inv_bonds", "inv_realestate", "inv_green_energy", "inv_tech", "inv_metals"
    val isActive: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
)

/**
 * User's Active or Completed Investment.
 * Valuation is computed based on real financial elapsed time and yield.
 */
@Entity(tableName = "user_investments")
data class UserInvestmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val productId: Long,
    val productTitleAz: String,
    val investedAmountCents: Long,
    val currentValuationCents: Long,
    val accruedProfitCents: Long,
    val annualYieldPercent: Double,
    val startDateMillis: Long,
    val maturityDateMillis: Long,
    val status: String = "Aktiv", // "Aktiv", "Tamamlandı", "Ləğv edildi"
    val lastCalculatedAtMillis: Long = System.currentTimeMillis(),
    val drawableResName: String = "inv_bonds"
)

/**
 * Universal Financial Transaction.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionIdCode: String, // e.g. "TXN-2026-98124"
    val userId: Long,
    val type: String, // "DEPOZİT", "ÇIXARIŞ", "İNVESTİSİYA", "GƏLİR", "KOMİSSİYA", "DÜZƏLİŞ"
    val amountCents: Long,
    val currency: String = "AZN",
    val feeCents: Long = 0L,
    val status: String, // "Gözləmədə", "Emal olunur", "Tamamlandı", "Uğursuz", "Ləğv edildi", "Rədd edildi"
    val paymentMethod: String, // "Bank Kartı", "BirBank / MilliÖN", "İBAN Köçürməsi", "Balans"
    val timestampMillis: Long = System.currentTimeMillis(),
    val notesAz: String = "",
    val referenceId: String = ""
)

/**
 * Deposit Request with Manual Payment Proof and Gateway Verification metadata.
 */
@Entity(tableName = "deposits")
data class DepositRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val depositIdCode: String, // e.g. "DEP-2026-98124"
    val userId: Long,
    val userName: String = "",
    val userEmail: String = "",
    val amountCents: Long, // e.g. 2500L = 25.00 AZN
    val paymentMethod: String = "Birbank / Kapital Bank",
    val status: String = "Gözləyir", // "Gözləyir", "Təsdiqləndi", "Rədd edildi"
    val referenceCode: String = "", // e.g. "KAP-893421"
    val receiptFileName: String = "", // "odenis_qebzi.jpg", "receipt.pdf"
    val receiptUri: String = "",
    val receiptBase64: String = "",
    val rejectionReasonAz: String = "",
    val adminReviewedBy: String = "",
    val adminReviewedAtMillis: Long? = null,
    val beforeBalanceCents: Long = 0L,
    val afterBalanceCents: Long = 0L,
    val gatewaySessionToken: String = "",
    val signatureVerificationCode: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val completedAtMillis: Long? = null
)

/**
 * Payment Card Entity stored in database.
 * Holds verified official platform card and user cards.
 */
@Entity(tableName = "payment_cards")
data class PaymentCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 0L, // 0 for platform official payment card
    val bankName: String = "Kapital Bank / Birbank",
    val cardNumber: String = "4169738849528363",
    val cardHolder: String = "VEYRA INVEST MMC",
    val expiryMonthYear: String = "12/28",
    val cvv: String = "836",
    val cardType: String = "VISA",
    val isOfficialPlatformAccount: Boolean = true,
    val isDefault: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis()
)

/**
 * Withdrawal Request with bank details and admin review status.
 */
@Entity(tableName = "withdrawals")
data class WithdrawalRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val withdrawalIdCode: String,
    val userId: Long,
    val amountCents: Long,
    val feeCents: Long,
    val netAmountCents: Long,
    val bankName: String,
    val iban: String,
    val cardLast4: String,
    val recipientFullName: String,
    val status: String, // "Gözləmədə", "Yoxlanılır", "Emal olunur", "Tamamlandı", "Rədd edildi", "Uğursuz"
    val rejectReasonAz: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val processedAtMillis: Long? = null
)

/**
 * Double-Entry Accounting Ledger Entry.
 * Every financial movement is immutably recorded with debit/credit accounts.
 */
@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryCode: String,
    val transactionCode: String,
    val debitAccount: String, // e.g. "USER_WALLET", "ESCROW_INVESTMENT", "GATEWAY_RECEIVABLES", "FEE_REVENUE"
    val creditAccount: String,
    val amountCents: Long,
    val descriptionAz: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val checksumHash: String = ""
)

/**
 * Portfolio historical snapshot for time interval graphing (1s, 24s, 7g, 30g, 3ay, 1il).
 */
@Entity(tableName = "portfolio_snapshots")
data class PortfolioSnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val timestampMillis: Long,
    val totalBalanceCents: Long,
    val investedCents: Long,
    val profitLossCents: Long,
    val intervalTag: String // "1H", "24H", "7D", "30D", "3M", "1Y"
)

/**
 * Audit Log for Admin & Compliance.
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adminEmail: String,
    val actionType: String,
    val targetType: String,
    val targetId: String,
    val detailsAz: String,
    val timestampMillis: Long = System.currentTimeMillis()
)
