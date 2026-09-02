package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.VeyraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

enum class VeyraScreen(val titleAz: String) {
    LANDING("Ana Səhifə"),
    AUTH("Daxil ol / Qeydiyyat"),
    DASHBOARD("Şəxsi Kabinet"),
    INVESTMENTS("İnvestisiyalar"),
    PORTFOLIO("Portfel"),
    DEPOSIT("Vəsait əlavə et"),
    WITHDRAWAL("Vəsaiti çıxar"),
    TRANSACTIONS("Əməliyyatlar"),
    ADMIN("Admin Panel"),
    LEGAL("Hüquqi Şəffaflıq")
}

data class VeyraUiState(
    val currentScreen: VeyraScreen = VeyraScreen.LANDING,
    val previousScreen: VeyraScreen? = null,
    val currentUser: UserEntity? = null,
    val selectedInterval: String = "30 gün",
    val chartSnapshots: List<PortfolioSnapshotEntity> = emptyList(),
    val isChartLoading: Boolean = false,
    val activeProducts: List<InvestmentProductEntity> = emptyList(),
    val allProducts: List<InvestmentProductEntity> = emptyList(),
    val userInvestments: List<UserInvestmentEntity> = emptyList(),
    val userTransactions: List<TransactionEntity> = emptyList(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val allUsers: List<UserEntity> = emptyList(),
    val pendingWithdrawals: List<WithdrawalRequestEntity> = emptyList(),
    val allWithdrawals: List<WithdrawalRequestEntity> = emptyList(),
    val allDeposits: List<DepositRequestEntity> = emptyList(),
    val pendingDeposits: List<DepositRequestEntity> = emptyList(),
    val userDeposits: List<DepositRequestEntity> = emptyList(),
    val officialDepositCard: PaymentCardEntity? = null,
    val userPaymentCards: List<PaymentCardEntity> = emptyList(),
    val ledgerEntries: List<LedgerEntryEntity> = emptyList(),
    val auditLogs: List<AuditLogEntity> = emptyList(),
    val transactionTypeFilter: String = "HAMISI",
    val transactionStatusFilter: String = "HAMISI",
    val isProcessingPayment: Boolean = false,
    val activeDepositSession: DepositRequestEntity? = null,
    val paymentSuccessMessage: String? = null,
    val latestApprovedDeposit: DepositRequestEntity? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class VeyraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = VeyraRepository(db.veyraDao())

    private val _uiState = MutableStateFlow(VeyraUiState())
    val uiState: StateFlow<VeyraUiState> = _uiState.asStateFlow()

    private var profitAccrualJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureVeyraHomeProducts()
        }
        observeData()
        startPeriodicProfitEngine()
    }

    private fun observeData() {
        // Observe Products
        viewModelScope.launch {
            repository.getActiveProductsFlow().collect { products ->
                _uiState.update { it.copy(activeProducts = products) }
            }
        }
        viewModelScope.launch {
            repository.getAllProductsFlow().collect { products ->
                _uiState.update { it.copy(allProducts = products) }
            }
        }
        // Observe All Users for Admin
        viewModelScope.launch {
            repository.getAllUsersFlow().collect { users ->
                _uiState.update { it.copy(allUsers = users) }
            }
        }
        // Observe All Transactions
        viewModelScope.launch {
            repository.getAllTransactionsFlow().collect { txns ->
                _uiState.update { it.copy(allTransactions = txns) }
            }
        }
        // Observe Pending Withdrawals
        viewModelScope.launch {
            repository.getPendingWithdrawalsFlow().collect { pw ->
                _uiState.update { it.copy(pendingWithdrawals = pw) }
            }
        }
        // Observe All Withdrawals
        viewModelScope.launch {
            repository.getAllWithdrawalsFlow().collect { aw ->
                _uiState.update { it.copy(allWithdrawals = aw) }
            }
        }
        // Observe All Deposits
        viewModelScope.launch {
            repository.getAllDepositsFlow().collect { ad ->
                _uiState.update { it.copy(allDeposits = ad) }
            }
        }
        // Observe Pending Deposits for Admin
        viewModelScope.launch {
            repository.getPendingDepositsFlow().collect { pd ->
                _uiState.update { it.copy(pendingDeposits = pd) }
            }
        }
        // Observe Official Payment Card
        viewModelScope.launch {
            val officialCard = repository.getOfficialDepositCard()
            _uiState.update { it.copy(officialDepositCard = officialCard) }
        }
        // Observe Ledger
        viewModelScope.launch {
            repository.getAllLedgerEntriesFlow().collect { le ->
                _uiState.update { it.copy(ledgerEntries = le) }
            }
        }
        // Observe Audit
        viewModelScope.launch {
            repository.getAllAuditLogsFlow().collect { al ->
                _uiState.update { it.copy(auditLogs = al) }
            }
        }
    }

    private fun startPeriodicProfitEngine() {
        profitAccrualJob?.cancel()
        profitAccrualJob = viewModelScope.launch {
            while (true) {
                val userId = _uiState.value.currentUser?.id
                if (userId != null) {
                    repository.calculateAndAccrueLiveProfits(userId)
                }
                delay(15000L) // updates every 15 seconds based on real yield
            }
        }
    }

    fun navigateTo(screen: VeyraScreen) {
        _uiState.update {
            it.copy(
                previousScreen = it.currentScreen,
                currentScreen = screen,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun loginDemoUser() {
        viewModelScope.launch {
            val user = repository.getUserByPhoneOrEmail("+994509876543", "rashad@example.com")
            if (user != null) {
                setCurrentUser(user)
                navigateTo(VeyraScreen.DASHBOARD)
            } else {
                val newUser = repository.registerOrLoginUser(
                    fullName = "Rəşad Əliyev",
                    phone = "+994509876543",
                    email = "rashad@example.com"
                )
                setCurrentUser(newUser)
                navigateTo(VeyraScreen.DASHBOARD)
            }
        }
    }

    fun loginAdmin() {
        viewModelScope.launch {
            val admin = repository.getUserByPhoneOrEmail("+994501234567", "admin@veyrainvest.az")
            if (admin != null) {
                setCurrentUser(admin)
                navigateTo(VeyraScreen.ADMIN)
            }
        }
    }

    fun loginWithCredentials(phoneOrEmail: String, otpOrPin: String, isGoogle: Boolean = false) {
        viewModelScope.launch {
            if (isGoogle) {
                val user = repository.registerOrLoginUser(
                    fullName = "Google İstifadəçisi",
                    phone = "+99450" + (1000000..9999999).random(),
                    email = if (phoneOrEmail.contains("@")) phoneOrEmail else "investor@gmail.com"
                )
                setCurrentUser(user)
                navigateTo(VeyraScreen.DASHBOARD)
                return@launch
            }

            if (phoneOrEmail.isBlank() || otpOrPin.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Zəhmət olmasa bütün xanaları doldurun.") }
                return@launch
            }

            // If phone or email matches admin
            if (phoneOrEmail.contains("admin") || phoneOrEmail == "+994501234567") {
                val admin = repository.getUserByPhoneOrEmail("+994501234567", "admin@veyrainvest.az")
                if (admin != null) {
                    setCurrentUser(admin)
                    navigateTo(VeyraScreen.ADMIN)
                    return@launch
                }
            }

            val user = repository.registerOrLoginUser(
                fullName = "İnvestor İstifadəçi",
                phone = if (phoneOrEmail.startsWith("+")) phoneOrEmail else "+994$phoneOrEmail",
                email = if (phoneOrEmail.contains("@")) phoneOrEmail else "user_${System.currentTimeMillis().toString().takeLast(4)}@veyrainvest.az"
            )
            setCurrentUser(user)
            navigateTo(VeyraScreen.DASHBOARD)
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                currentScreen = VeyraScreen.LANDING,
                userInvestments = emptyList(),
                userTransactions = emptyList(),
                chartSnapshots = emptyList()
            )
        }
    }

    private fun setCurrentUser(user: UserEntity) {
        _uiState.update { it.copy(currentUser = user) }

        // Start collecting user specific flows
        viewModelScope.launch {
            repository.getUserByIdFlow(user.id).collect { updatedUser ->
                _uiState.update { it.copy(currentUser = updatedUser) }
            }
        }
        viewModelScope.launch {
            repository.getUserInvestmentsFlow(user.id).collect { investments ->
                _uiState.update { it.copy(userInvestments = investments) }
            }
        }
        viewModelScope.launch {
            repository.getUserTransactionsFlow(user.id).collect { txns ->
                _uiState.update { it.copy(userTransactions = txns) }
            }
        }
        viewModelScope.launch {
            repository.getUserDepositsFlow(user.id).collect { deposits ->
                _uiState.update { it.copy(userDeposits = deposits) }
            }
        }
        viewModelScope.launch {
            repository.getPaymentCardsFlow(user.id).collect { cards ->
                _uiState.update { it.copy(userPaymentCards = cards) }
            }
        }

        loadChartSnapshots(_uiState.value.selectedInterval)
    }

    fun selectTimeInterval(interval: String) {
        _uiState.update { it.copy(selectedInterval = interval) }
        loadChartSnapshots(interval)
    }

    private fun loadChartSnapshots(interval: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isChartLoading = true) }
            val snapshots = repository.generateSnapshotsForInterval(user.id, interval)
            _uiState.update { it.copy(chartSnapshots = snapshots, isChartLoading = false) }
        }
    }

    fun submitKycVerification(docNo: String, finCode: String) {
        val user = _uiState.value.currentUser ?: return
        if (docNo.isBlank() || finCode.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Zəhmət olmasa sənəd nömrəsi və FİN kodu daxil edin.") }
            return
        }
        viewModelScope.launch {
            repository.submitKyc(user.id, docNo.uppercase(Locale.ROOT), finCode.uppercase(Locale.ROOT))
            _uiState.update { it.copy(infoMessage = "Şəxsiyyət məlumatlarınız (KYC) uğurla təsdiqləndi.") }
        }
    }

    // Deposit Flow - Manual Verification
    fun submitManualDeposit(
        amountAz: Double,
        referenceCode: String,
        receiptFileName: String,
        receiptUri: String = "",
        paymentDateMillis: Long = System.currentTimeMillis()
    ) {
        val user = _uiState.value.currentUser ?: return
        if (amountAz < 25.0) {
            _uiState.update { it.copy(errorMessage = "Minimum depozit məbləği 25.00 AZN-dir.") }
            return
        }
        val amountCents = (amountAz * 100).toLong()

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true, errorMessage = null) }
            val result = repository.createManualDepositRequest(
                userId = user.id,
                amountCents = amountCents,
                referenceCode = referenceCode,
                receiptFileName = receiptFileName,
                receiptUri = receiptUri,
                paymentDateMillis = paymentDateMillis
            )
            result.onSuccess { depositEntity ->
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        activeDepositSession = depositEntity,
                        infoMessage = "Depozit sorğunuz uğurla göndərildi! Status: 🟡 Təsdiq gözləyir"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        errorMessage = error.message ?: "Depozit sorğusu göndərilə bilmədi"
                    )
                }
            }
        }
    }

    fun startDeposit(amountAz: Double, paymentMethod: String) {
        submitManualDeposit(
            amountAz = amountAz,
            referenceCode = "KAP-" + (100000..999999).random(),
            receiptFileName = "birbank_odenis_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
        )
    }

    fun confirmDepositGatewayCallback(simulatedFail: Boolean = false) {
        val session = _uiState.value.activeDepositSession ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            val result = repository.completeDepositThroughWebhook(session.depositIdCode, simulatedFail)
            result.onSuccess { msg ->
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        activeDepositSession = null,
                        paymentSuccessMessage = "Depozit uğurla tamamlandı və balansınıza əlavə edildi!",
                        infoMessage = msg
                    )
                }
                loadChartSnapshots(_uiState.value.selectedInterval)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        activeDepositSession = null,
                        errorMessage = error.message ?: "Ödəniş təsdiqlənmədi"
                    )
                }
            }
        }
    }

    fun dismissDepositModal() {
        _uiState.update { it.copy(activeDepositSession = null, paymentSuccessMessage = null) }
    }

    // Withdrawal Flow
    fun requestWithdrawal(
        amountAz: Double,
        bankName: String,
        iban: String,
        cardLast4: String,
        recipientName: String
    ) {
        val user = _uiState.value.currentUser ?: return
        val amountCents = (amountAz * 100).toLong()

        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
            val result = repository.requestWithdrawal(
                userId = user.id,
                amountCents = amountCents,
                bankName = bankName,
                iban = iban,
                cardLast4 = cardLast4,
                recipientName = recipientName
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        infoMessage = "Çıxarış sorğunuz qeydə alındı və maliyyə yoxlanışına göndərildi.",
                        currentScreen = VeyraScreen.TRANSACTIONS
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Çıxarış sorğusu yaradıla bilmədi") }
            }
        }
    }

    // Invest in Product
    fun makeInvestment(productId: Long, amountAz: Double) {
        val user = _uiState.value.currentUser ?: return
        val amountCents = (amountAz * 100).toLong()

        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
            val result = repository.createInvestment(user.id, productId, amountCents)
            result.onSuccess { inv ->
                _uiState.update {
                    it.copy(
                        infoMessage = "${inv.productTitleAz} məhsuluna ${amountAz} AZN investisiya uğurla yatırıldı!",
                        currentScreen = VeyraScreen.PORTFOLIO
                    )
                }
                loadChartSnapshots(_uiState.value.selectedInterval)
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "İnvestisiya əməliyyatı uğursuz oldu") }
            }
        }
    }

    // Transaction Filtering
    fun setTransactionFilters(type: String, status: String) {
        _uiState.update {
            it.copy(
                transactionTypeFilter = type,
                transactionStatusFilter = status
            )
        }
    }

    // Admin Controls
    fun adminApproveWithdrawal(withdrawalId: Long) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val result = repository.adminProcessWithdrawal(admin.email, withdrawalId, approve = true)
            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "Çıxarış sorğusu təsdiqləndi və vəsait köçürüldü.") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminRejectWithdrawal(withdrawalId: Long, reason: String) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val result = repository.adminProcessWithdrawal(admin.email, withdrawalId, approve = false, rejectReasonAz = reason)
            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "Çıxarış sorğusu rədd edildi və vəsait istifadəçi balansına qaytarıldı.") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message) }
            }
        }
    }

    fun adminToggleUserStatus(userId: Long, newStatus: Boolean) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.toggleUserActiveStatus(admin.email, userId, newStatus)
            _uiState.update { it.copy(infoMessage = "İstifadəçi statusu dəyişdirildi.") }
        }
    }

    fun adminSaveProduct(product: InvestmentProductEntity) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.saveOrUpdateProduct(admin.email, product)
            _uiState.update { it.copy(infoMessage = "İnvestisiya məhsulu uğurla yadda saxlanıldı.") }
        }
    }

    fun adminApproveDeposit(depositId: Long) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
            val result = repository.adminApproveDeposit(admin.email, depositId)
            result.onSuccess { dep ->
                _uiState.update {
                    it.copy(
                        latestApprovedDeposit = dep,
                        infoMessage = "Depozit təsdiqləndi! +${dep.amountCents / 100.0} AZN istifadəçi (${dep.userName}) balansına əlavə edildi."
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Təsdiq xətası baş verdi") }
            }
        }
    }

    fun adminRejectDeposit(depositId: Long, reason: String) {
        val admin = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
            val result = repository.adminRejectDeposit(admin.email, depositId, reason)
            result.onSuccess {
                _uiState.update { it.copy(infoMessage = "Depozit sorğusu rədd edildi və səbəb qeyd olundu.") }
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Rədd etmə xətası") }
            }
        }
    }

    fun saveUserPaymentCard(bankName: String, cardNumber: String, cardHolder: String, expiry: String, cvv: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val cleanNumber = cardNumber.replace(" ", "")
            repository.savePaymentCard(
                PaymentCardEntity(
                    userId = user.id,
                    bankName = bankName.ifBlank { "Bank Kartı" },
                    cardNumber = cleanNumber,
                    cardHolder = cardHolder.uppercase(),
                    expiryMonthYear = expiry,
                    cvv = cvv,
                    cardType = if (cleanNumber.startsWith("4")) "VISA" else "MASTERCARD",
                    isOfficialPlatformAccount = false,
                    isDefault = false
                )
            )
            _uiState.update { it.copy(infoMessage = "Kart məlumatları yadda saxlanıldı.") }
        }
    }

    fun clearLatestApprovedDeposit() {
        _uiState.update { it.copy(latestApprovedDeposit = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }
}
