package com.example

import android.accounts.AccountManager
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.TransactionEntity
import com.example.data.model.VeyraHomeConfig
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.VeyraScreen
import com.example.ui.viewmodel.VeyraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VeyraInvestApp()
            }
        }
    }
}

@Composable
fun VeyraInvestApp(
    viewModel: VeyraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTransactionForDetail by remember { mutableStateOf<TransactionEntity?>(null) }
    var isStartupRunning by remember { mutableStateOf(true) }

    // Native Google Account Picker launcher
    val googleAccountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                viewModel.loginWithGmail(accountName, null, isGoogleAccount = true)
            } else {
                viewModel.navigateTo(VeyraScreen.AUTH)
            }
        } else {
            viewModel.navigateTo(VeyraScreen.AUTH)
        }
    }

    // Display info messages
    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    // Display error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err)
            viewModel.clearMessages()
        }
    }

    // Handle Android system back button
    BackHandler(enabled = uiState.currentScreen != VeyraScreen.LANDING && uiState.currentScreen != VeyraScreen.DASHBOARD && !isStartupRunning) {
        if (uiState.currentUser != null) {
            viewModel.navigateTo(VeyraScreen.DASHBOARD)
        } else {
            viewModel.navigateTo(VeyraScreen.LANDING)
        }
    }

    // Show full-screen Startup Animation on launch
    if (isStartupRunning) {
        StartupSplashScreen(
            onAnimationComplete = {
                isStartupRunning = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                if (uiState.currentScreen != VeyraScreen.LANDING) {
                    VeyraTopBar(
                        currentScreen = uiState.currentScreen,
                        user = uiState.currentUser,
                        onNavigate = { viewModel.navigateTo(it) },
                        onLogout = { viewModel.logout() }
                    )
                }
            },
            bottomBar = {
                if (uiState.currentUser != null && uiState.currentScreen != VeyraScreen.ADMIN && uiState.currentScreen != VeyraScreen.LANDING) {
                    VeyraBottomBar(
                        currentScreen = uiState.currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = VeyraNavyElevated,
                            contentColor = VeyraTextPrimary,
                            actionColor = VeyraEmerald
                        )
                    }
                )
            },
            containerColor = VeyraNavyDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (uiState.currentScreen == VeyraScreen.LANDING) PaddingValues(0.dp) else innerPadding)
                    .background(VeyraNavyDark)
            ) {
                when (uiState.currentScreen) {
                    VeyraScreen.LANDING -> {
                        LandingScreen(
                            featuredProducts = uiState.activeProducts,
                            onExploreInvestments = {
                                if (uiState.currentUser != null) {
                                    viewModel.navigateTo(VeyraScreen.INVESTMENTS)
                                } else {
                                    viewModel.navigateTo(VeyraScreen.AUTH)
                                }
                            },
                            onLoginClick = { viewModel.navigateTo(VeyraScreen.AUTH) },
                            onGoogleLoginClick = {
                                try {
                                    val intent = AccountManager.newChooseAccountIntent(
                                        null,
                                        null,
                                        arrayOf("com.google"),
                                        false,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                    googleAccountPickerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    viewModel.navigateTo(VeyraScreen.AUTH)
                                }
                            },
                            onProductClick = {
                                if (uiState.currentUser != null) {
                                    viewModel.navigateTo(VeyraScreen.INVESTMENTS)
                                } else {
                                    viewModel.navigateTo(VeyraScreen.AUTH)
                                }
                            },
                            onLegalClick = { viewModel.navigateTo(VeyraScreen.LEGAL) }
                        )
                    }

                    VeyraScreen.AUTH -> {
                        AuthScreen(
                            onLoginSuccess = { email, fullName, isGoogle ->
                                viewModel.loginWithGmail(email, fullName, isGoogle)
                            },
                            errorMessage = uiState.errorMessage
                        )
                    }

                    VeyraScreen.DASHBOARD -> {
                        uiState.currentUser?.let { user ->
                            DashboardScreen(
                                user = user,
                                userInvestments = uiState.userInvestments,
                                userTransactions = uiState.userTransactions,
                                snapshots = uiState.chartSnapshots,
                                selectedInterval = uiState.selectedInterval,
                                isChartLoading = uiState.isChartLoading,
                                products = uiState.activeProducts,
                                onIntervalSelected = { viewModel.selectTimeInterval(it) },
                                onDepositClick = { viewModel.navigateTo(VeyraScreen.DEPOSIT) },
                                onWithdrawalClick = { viewModel.navigateTo(VeyraScreen.WITHDRAWAL) },
                                onInvestmentsClick = { viewModel.navigateTo(VeyraScreen.INVESTMENTS) },
                                onPortfolioClick = { viewModel.navigateTo(VeyraScreen.PORTFOLIO) },
                                onTransactionsClick = { viewModel.navigateTo(VeyraScreen.TRANSACTIONS) },
                                onKycSubmit = { doc, fin -> viewModel.submitKycVerification(doc, fin) },
                                onTransactionDetailClick = { txn ->
                                    selectedTransactionForDetail = txn
                                    viewModel.navigateTo(VeyraScreen.TRANSACTIONS)
                                },
                                onMakeInvestment = { pId, amt -> viewModel.makeInvestment(pId, amt) }
                            )
                        } ?: run {
                            viewModel.navigateTo(VeyraScreen.AUTH)
                        }
                    }

                    VeyraScreen.INVESTMENTS -> {
                        InvestmentsScreen(
                            user = uiState.currentUser,
                            products = uiState.activeProducts,
                            onInvestSubmit = { pId, amt -> viewModel.makeInvestment(pId, amt) },
                            onDepositRedirect = { viewModel.navigateTo(VeyraScreen.DEPOSIT) }
                        )
                    }

                    VeyraScreen.PORTFOLIO -> {
                        PortfolioScreen(
                            user = uiState.currentUser,
                            investments = uiState.userInvestments,
                            onExploreClick = { viewModel.navigateTo(VeyraScreen.INVESTMENTS) }
                        )
                    }

                    VeyraScreen.DEPOSIT -> {
                        DepositScreen(
                            user = uiState.currentUser,
                            userDeposits = uiState.userDeposits,
                            officialCard = uiState.officialDepositCard,
                            userSavedCards = uiState.userPaymentCards,
                            onSubmitManualDeposit = { amt, ref, receiptName, uri, date ->
                                viewModel.submitManualDeposit(amt, ref, receiptName, uri, date)
                            },
                            onSavePaymentCard = { bank, num, holder, exp, cvv ->
                                viewModel.saveUserPaymentCard(bank, num, holder, exp, cvv)
                            },
                            isProcessing = uiState.isProcessingPayment,
                            errorMessage = uiState.errorMessage,
                            infoMessage = uiState.infoMessage,
                            onGoToTransactions = { viewModel.navigateTo(VeyraScreen.TRANSACTIONS) }
                        )
                    }

                    VeyraScreen.WITHDRAWAL -> {
                        WithdrawalScreen(
                            user = uiState.currentUser,
                            onRequestWithdrawal = { amt, bank, iban, card, name ->
                                viewModel.requestWithdrawal(amt, bank, iban, card, name)
                            },
                            onKycRedirect = { viewModel.navigateTo(VeyraScreen.DASHBOARD) },
                            errorMessage = uiState.errorMessage
                        )
                    }

                    VeyraScreen.TRANSACTIONS -> {
                        TransactionsScreen(
                            transactions = if (uiState.currentUser?.role == "ADMIN") uiState.allTransactions else uiState.userTransactions,
                            typeFilter = uiState.transactionTypeFilter,
                            statusFilter = uiState.transactionStatusFilter,
                            onFilterChange = { t, s -> viewModel.setTransactionFilters(t, s) },
                            selectedTransactionForDetail = selectedTransactionForDetail,
                            onSelectTransaction = { selectedTransactionForDetail = it }
                        )
                    }

                    VeyraScreen.ADMIN -> {
                        AdminScreen(
                            users = uiState.allUsers,
                            products = uiState.allProducts,
                            pendingDeposits = uiState.pendingDeposits,
                            allDeposits = uiState.allDeposits,
                            pendingWithdrawals = uiState.pendingWithdrawals,
                            allWithdrawals = uiState.allWithdrawals,
                            transactions = uiState.allTransactions,
                            ledgerEntries = uiState.ledgerEntries,
                            auditLogs = uiState.auditLogs,
                            onApproveDeposit = { viewModel.adminApproveDeposit(it) },
                            onRejectDeposit = { id, reason -> viewModel.adminRejectDeposit(id, reason) },
                            onApproveWithdrawal = { viewModel.adminApproveWithdrawal(it) },
                            onRejectWithdrawal = { id, reason -> viewModel.adminRejectWithdrawal(id, reason) },
                            onToggleUserStatus = { id, status -> viewModel.adminToggleUserStatus(id, status) },
                            onSaveProduct = { viewModel.adminSaveProduct(it) }
                        )
                    }

                    VeyraScreen.LEGAL -> {
                        LegalScreen()
                    }
                }
            }
        }

        // Deposit Approved Celebration Modal
        uiState.latestApprovedDeposit?.let { approvedDeposit ->
            val depositAz = approvedDeposit.amountCents / 100.0
            val currentBalAz = (uiState.currentUser?.balanceCents ?: 0L) / 100.0
            val stageCalc = VeyraHomeConfig.calculateHomeStage(depositAz)
            DepositCelebrationDialog(
                amountAz = depositAz,
                newBalanceAz = currentBalAz,
                homeStageTitle = stageCalc.currentStageTitle,
                onDismiss = { viewModel.clearLatestApprovedDeposit() }
            )
        }

        // Payment Gateway Modal Dialog (Sandbox 3D Secure / Webhook Verification)
        uiState.activeDepositSession?.let { session ->
            PaymentGatewayModal(
                depositSession = session,
                isProcessing = uiState.isProcessingPayment,
                onConfirmPayment = { simulatedFail ->
                    viewModel.confirmDepositGatewayCallback(simulatedFail)
                },
                onDismiss = { viewModel.dismissDepositModal() }
            )
        }
    }
}
