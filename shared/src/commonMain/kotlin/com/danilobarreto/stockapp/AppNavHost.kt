package com.danilobarreto.stockapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.presentation.ForgotPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.NewPasswordScreen
import com.danilobarreto.stockapp.auth.presentation.PasswordResetViewModel
import com.danilobarreto.stockapp.auth.presentation.ProfileScreen
import com.danilobarreto.stockapp.auth.presentation.ProfileUiState
import com.danilobarreto.stockapp.auth.presentation.ProfileViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.auth.presentation.ResetCodeScreen
import com.danilobarreto.stockapp.imports.presentation.ImportScreen
import com.danilobarreto.stockapp.imports.presentation.ImportViewModel
import com.danilobarreto.stockapp.orders.presentation.OrderBottomSheet
import com.danilobarreto.stockapp.orders.presentation.OrderFormViewModel
import com.danilobarreto.stockapp.portfolio.presentation.DashboardScreen
import com.danilobarreto.stockapp.portfolio.presentation.DashboardUiState
import com.danilobarreto.stockapp.portfolio.presentation.DashboardViewModel
import com.danilobarreto.stockapp.portfolio.presentation.HomeScreen
import com.danilobarreto.stockapp.portfolio.presentation.HomeViewModel
import com.danilobarreto.stockapp.quotes.presentation.AssetQuotesScreen
import com.danilobarreto.stockapp.quotes.presentation.AssetType
import com.danilobarreto.stockapp.quotes.presentation.FiisViewModel
import com.danilobarreto.stockapp.quotes.presentation.QuotesViewModel
import com.danilobarreto.stockapp.valuation.domain.AssetValuationInput
import com.danilobarreto.stockapp.valuation.presentation.ValuationListScreen
import com.danilobarreto.stockapp.valuation.presentation.ValuationListViewModel
import com.danilobarreto.stockapp.valuation.presentation.ValuationScreen
import com.danilobarreto.stockapp.valuation.presentation.ValuationViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppFab
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets

private enum class MainTab { Home, Portfolio, Quotes, Profile }

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    authRepository: AuthRepositoryImpl,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    quotesViewModel: QuotesViewModel,
    fiisViewModel: FiisViewModel,
    dashboardViewModel: DashboardViewModel,
    orderFormViewModel: OrderFormViewModel,
    importViewModel: ImportViewModel,
    passwordResetViewModel: PasswordResetViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
) {
    var valuationListViewModel by remember { mutableStateOf<ValuationListViewModel?>(null) }
    var valuationItems by remember { mutableStateOf<List<AssetValuationInput>>(emptyList()) }
    var valuationViewModel by remember { mutableStateOf<ValuationViewModel?>(null) }
    var selectedTab by remember { mutableStateOf(MainTab.Home) }
    var selectedAssetType by remember { mutableStateOf(AssetType.Stock) }

    val openValuation: (AssetValuationInput) -> Unit = { item ->
        valuationViewModel = ValuationViewModel(item.fundamentals)
        navController.navigate(Valuation(ticker = item.ticker))
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable<Login> {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Home) { popUpTo<Login> { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Register) },
                onForgotPassword = { navController.navigate(ForgotPassword) },
            )
        }
        composable<Register> {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Home) { popUpTo<Login> { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable<ForgotPassword> {
            ForgotPasswordScreen(
                viewModel = passwordResetViewModel,
                onBack = { navController.popBackStack() },
                onCodeSent = { navController.navigate(ResetCode) },
            )
        }
        composable<ResetCode> {
            ResetCodeScreen(
                viewModel = passwordResetViewModel,
                onBack = { navController.popBackStack() },
                onCodeValidated = { navController.navigate(NewPassword) },
            )
        }
        composable<NewPassword> {
            NewPasswordScreen(
                viewModel = passwordResetViewModel,
                onBack = { navController.popBackStack() },
                onPasswordReset = {
                    navController.navigate(Login) { popUpTo<Login> { inclusive = true } }
                },
            )
        }
        composable<Home> {
            MainShell(
                navController = navController,
                authRepository = authRepository,
                quotesViewModel = quotesViewModel,
                fiisViewModel = fiisViewModel,
                dashboardViewModel = dashboardViewModel,
                orderFormViewModel = orderFormViewModel,
                homeViewModel = homeViewModel,
                profileViewModel = profileViewModel,
                onOpenValuationList = { items ->
                    valuationItems = items
                    valuationListViewModel = ValuationListViewModel(items)
                    navController.navigate(ValuationList)
                },
                onOpenValuation = openValuation,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                selectedAssetType = selectedAssetType,
                onAssetTypeSelected = { selectedAssetType = it }
            )
        }
        composable<Import> {
            ImportScreen(viewModel = importViewModel, onBack = { navController.popBackStack() })
        }
        composable<ValuationList> {
            valuationListViewModel?.let { vm ->
                ValuationListScreen(
                    viewModel = vm,
                    onItemClick = { ticker ->
                        valuationItems.find { it.ticker == ticker }?.let(openValuation)
                    },
                    onBack = { navController.popBackStack() },
                )
            }
        }
        composable<Valuation> { backStackEntry ->
            val args = backStackEntry.toRoute<Valuation>()
            valuationViewModel?.let { vm ->
                ValuationScreen(viewModel = vm, ticker = args.ticker, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun MainShell(
    navController: NavHostController,
    authRepository: AuthRepositoryImpl,
    quotesViewModel: QuotesViewModel,
    fiisViewModel: FiisViewModel,
    dashboardViewModel: DashboardViewModel,
    orderFormViewModel: OrderFormViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    onOpenValuationList: (List<AssetValuationInput>) -> Unit,
    onOpenValuation: (AssetValuationInput) -> Unit,
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    selectedAssetType: AssetType,
    onAssetTypeSelected: (AssetType) -> Unit
) {
    var showQuickOrder by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val userName = (profileState as? ProfileUiState.Success)?.profile?.name ?: ""

    SetStatusBarAppearance(useLightIcons = selectedTab == MainTab.Home)

    val openValuationFromDashboard: () -> Unit = {
        val state = dashboardState
        if (state is DashboardUiState.Success) {
            onOpenValuationList(state.summary.positions.map { it.toAssetValuationInput() })
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onFabClick = {
                    orderFormViewModel.reset()
                    showQuickOrder = true
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen(
                    userName = userName,
                    viewModel = homeViewModel,
                    onNovaOrdem = {
                        orderFormViewModel.reset()
                        showQuickOrder = true
                    },
                    onImportarB3 = { navController.navigate(Import) },
                    onValuation = openValuationFromDashboard,
                    onCotacoes = { onTabSelected(MainTab.Quotes) },
                    onVerCarteira = { onTabSelected(MainTab.Portfolio) },
                )
                MainTab.Quotes -> AssetQuotesScreen(
                    quotesViewModel = quotesViewModel,
                    fiisViewModel = fiisViewModel,
                    onViewStockValuation = { onOpenValuation(it.toAssetValuationInput()) },
                    onViewFiiValuation = { onOpenValuation(it.toAssetValuationInput()) },
                    selectedAssetType = selectedAssetType,
                    onAssetTypeSelected = onAssetTypeSelected
                )
                MainTab.Portfolio -> {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onAddOrder = {
                            orderFormViewModel.reset()
                            showQuickOrder = true
                        },
                        onImport = { navController.navigate(Import) },
                        onViewValuation = openValuationFromDashboard,
                    )
                }
                MainTab.Profile -> ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = {
                        coroutineScope.launch {
                            authRepository.logout()
                            navController.navigate(Login) { popUpTo(navController.graph.id) { inclusive = true } }
                        }
                    },
                )
            }
        }
        if (showQuickOrder) {
            OrderBottomSheet(
                viewModel = orderFormViewModel,
                onDismiss = { showQuickOrder = false },
                onSaved = {
                    showQuickOrder = false
                    dashboardViewModel.load()
                },
            )
        }
    }
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onFabClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StockAppColors.surface2)
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomBarItem(
                icon = StockAppIcons.Home,
                label = "Início",
                selected = selectedTab == MainTab.Home,
                onClick = { onTabSelected(MainTab.Home) },
                modifier = Modifier.weight(1f),
            )
            BottomBarItem(
                icon = StockAppIcons.Wallet,
                label = "Carteira",
                selected = selectedTab == MainTab.Portfolio,
                onClick = { onTabSelected(MainTab.Portfolio) },
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomBarItem(
                icon = StockAppIcons.ChartCandle,
                label = "Cotações",
                selected = selectedTab == MainTab.Quotes,
                onClick = { onTabSelected(MainTab.Quotes) },
                modifier = Modifier.weight(1f),
            )
            BottomBarItem(
                icon = StockAppIcons.User,
                label = "Perfil",
                selected = selectedTab == MainTab.Profile,
                onClick = { onTabSelected(MainTab.Profile) },
                modifier = Modifier.weight(1f),
            )
        }
        StockAppFab(
            onClick = onFabClick,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-20).dp),
        )
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) StockAppColors.primary else StockAppColors.textMuted,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = StockAppTypography.labelSmall,
            color = if (selected) StockAppColors.primary else StockAppColors.textMuted,
        )
    }
}