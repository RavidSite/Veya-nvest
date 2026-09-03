package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VeyraDao {

    // User Operations
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone OR email = :email LIMIT 1")
    suspend fun getUserByPhoneOrEmail(phone: String, email: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registeredAtMillis DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balanceCents = balanceCents + :deltaCents WHERE id = :userId")
    suspend fun adjustUserBalance(userId: Long, deltaCents: Long)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun setUserActiveStatus(userId: Long, isActive: Boolean)

    @Query("UPDATE users SET kycStatus = :status, kycDocumentNo = :docNo, kycFinCode = :fin WHERE id = :userId")
    suspend fun updateUserKyc(userId: Long, status: String, docNo: String, fin: String)

    // Investment Products
    @Query("SELECT * FROM investment_products WHERE isActive = 1 ORDER BY minAmountCents ASC")
    fun getActiveProductsFlow(): Flow<List<InvestmentProductEntity>>

    @Query("SELECT * FROM investment_products ORDER BY id ASC")
    fun getAllProductsFlow(): Flow<List<InvestmentProductEntity>>

    @Query("SELECT * FROM investment_products ORDER BY minAmountCents ASC")
    suspend fun getAllProducts(): List<InvestmentProductEntity>

    @Query("SELECT * FROM investment_products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): InvestmentProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: InvestmentProductEntity): Long

    @Update
    suspend fun updateProduct(product: InvestmentProductEntity)

    // User Investments
    @Query("SELECT * FROM user_investments WHERE userId = :userId ORDER BY startDateMillis DESC")
    fun getUserInvestmentsFlow(userId: Long): Flow<List<UserInvestmentEntity>>

    @Query("SELECT * FROM user_investments WHERE userId = :userId AND status = 'Aktiv'")
    suspend fun getActiveUserInvestments(userId: Long): List<UserInvestmentEntity>

    @Query("SELECT * FROM user_investments WHERE status = 'Aktiv'")
    suspend fun getAllActiveInvestments(): List<UserInvestmentEntity>

    @Query("SELECT * FROM user_investments WHERE id = :id LIMIT 1")
    suspend fun getUserInvestmentById(id: Long): UserInvestmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInvestment(investment: UserInvestmentEntity): Long

    @Update
    suspend fun updateUserInvestment(investment: UserInvestmentEntity)

    // Transactions
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestampMillis DESC")
    fun getUserTransactionsFlow(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestampMillis DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    // Deposits
    @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY createdAtMillis DESC")
    fun getUserDepositsFlow(userId: Long): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposits ORDER BY createdAtMillis DESC")
    fun getAllDepositsFlow(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposits WHERE status IN ('Gözləyir', 'Gözləmədə') ORDER BY createdAtMillis ASC")
    fun getPendingDepositsFlow(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposits WHERE id = :id LIMIT 1")
    suspend fun getDepositById(id: Long): DepositRequestEntity?

    @Query("SELECT * FROM deposits WHERE depositIdCode = :code LIMIT 1")
    suspend fun getDepositByCode(code: String): DepositRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositRequestEntity): Long

    @Update
    suspend fun updateDeposit(deposit: DepositRequestEntity)

    // Payment Cards (Birbank / Kapital Bank & Saved User Cards)
    @Query("SELECT * FROM payment_cards WHERE isOfficialPlatformAccount = 1 LIMIT 1")
    suspend fun getOfficialPlatformCard(): PaymentCardEntity?

    @Query("SELECT * FROM payment_cards WHERE userId = :userId OR isOfficialPlatformAccount = 1 ORDER BY isOfficialPlatformAccount DESC, id ASC")
    fun getPaymentCardsFlow(userId: Long): Flow<List<PaymentCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentCard(card: PaymentCardEntity): Long

    @Query("SELECT COUNT(*) FROM payment_cards")
    suspend fun getPaymentCardCount(): Int

    // Withdrawals
    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY createdAtMillis DESC")
    fun getUserWithdrawalsFlow(userId: Long): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY createdAtMillis DESC")
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawals WHERE status IN ('Gözləmədə', 'Yoxlanılır') ORDER BY createdAtMillis ASC")
    fun getPendingWithdrawalsFlow(): Flow<List<WithdrawalRequestEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalById(id: Long): WithdrawalRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalRequestEntity): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalRequestEntity)

    // Ledger Entries
    @Query("SELECT * FROM ledger_entries ORDER BY timestampMillis DESC")
    fun getAllLedgerEntriesFlow(): Flow<List<LedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity): Long

    // Portfolio Snapshots
    @Query("SELECT * FROM portfolio_snapshots WHERE userId = :userId ORDER BY timestampMillis ASC")
    fun getPortfolioSnapshotsFlow(userId: Long): Flow<List<PortfolioSnapshotEntity>>

    @Query("SELECT * FROM portfolio_snapshots WHERE userId = :userId AND intervalTag = :tag ORDER BY timestampMillis ASC")
    fun getPortfolioSnapshotsByTagFlow(userId: Long, tag: String): Flow<List<PortfolioSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioSnapshot(snapshot: PortfolioSnapshotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioSnapshots(snapshots: List<PortfolioSnapshotEntity>)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestampMillis DESC LIMIT 200")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
