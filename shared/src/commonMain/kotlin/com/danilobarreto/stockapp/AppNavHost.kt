package com.danilobarreto.stockapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.imports.presentation.ImportScreen
import com.danilobarreto.stockapp.imports.presentation.ImportViewModel
import com.danilobarreto.stockapp.orders.presentation.OrderBottomSheet
import com.danilobarreto.stockapp.orders.presentation.OrderFormViewModel
import com.danilobarreto.stockapp.portfolio.presentation.DashboardScreen
import com.danilobarreto.stockapp.portfolio.presentation.DashboardUiState
import com.danilobarreto.stockapp.portfolio.presentation.DashboardViewModel
import com.danilobarreto.stockapp.quotes.presentation.AssetQuotesScreen
import com.danilobarreto.stockapp.quotes.presentation.FiisViewModel
import com.danilobarreto.stockapp.quotes.presentation.QuotesViewModel
import com.danilobarreto.stockapp.valuation.domain.AssetValuationInput
import com.danilobarreto.stockapp.valuation.presentation.ValuationListScreen
import com.danilobarreto.stockapp.valuation.presentation.ValuationListViewModel
import com.danilobarreto.stockapp.valuation.presentation.ValuationScreen
import com.danilobarreto.stockapp.valuation.presentation.ValuationViewModel
import kotlinx.coroutines.launch

private enum class MainTab { Quotes, Portfolio }

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
) {
    // "Porta-objeto" da listagem de valuation: preenchido no clique de quem
    // dispara a navegação (hoje só DashboardScreen; amanhã uma tela de
    // observados poderia preencher do mesmo jeito), lido pela rota genérica
    // abaixo. Não é @Serializable de propósito — não é dado de rota, é
    // estado computado passado adiante.
    var valuationListViewModel by remember { mutableStateOf<ValuationListViewModel?>(null) }
    // Guarda a lista crua que originou a listagem, só pra resolver o clique
    // num item (a listagem só devolve o ticker, não o AssetValuationInput
    // inteiro — ver ValuationListScreen.onItemClick).
    var valuationItems by remember { mutableStateOf<List<AssetValuationInput>>(emptyList()) }
    // Porta-objeto da tela de valuation individual (entrada única, seja pela
    // listagem da carteira ou direto pelo ticker na aba Cotações).
    var valuationViewModel by remember { mutableStateOf<ValuationViewModel?>(null) }
    // AppNavHost.kt — junto dos outros porta-objeto (antes do NavHost):
    var selectedTab by remember { mutableStateOf(MainTab.Quotes) }

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
            )
        }
        composable<Register> {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Home) { popUpTo<Login> { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable<Home> {
            HomeScreen(
                navController = navController,
                authRepository = authRepository,
                quotesViewModel = quotesViewModel,
                fiisViewModel = fiisViewModel,
                dashboardViewModel = dashboardViewModel,
                orderFormViewModel = orderFormViewModel,
                onOpenValuationList = { items ->
                    valuationItems = items
                    valuationListViewModel = ValuationListViewModel(items)
                    navController.navigate(ValuationList)
                },
                onOpenValuation = openValuation,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        }
        composable<Import> {
            ImportScreen(viewModel = importViewModel, onBack = { navController.popBackStack() })
        }
        composable<ValuationList> {
            // Genérica de propósito: não sabe se o dado veio do dashboard,
            // de uma watchlist ou de qualquer outra tela futura.
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
private fun HomeScreen(
    navController: NavHostController,
    authRepository: AuthRepositoryImpl,
    quotesViewModel: QuotesViewModel,
    fiisViewModel: FiisViewModel,
    dashboardViewModel: DashboardViewModel,
    orderFormViewModel: OrderFormViewModel,
    onOpenValuationList: (List<AssetValuationInput>) -> Unit,
    onOpenValuation: (AssetValuationInput) -> Unit,
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
) {
    var showQuickOrder by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val dashboardState by dashboardViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedTab == MainTab.Quotes) "Cotações" else "Carteira") },
                actions = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            authRepository.logout()
                            navController.navigate(Login) { popUpTo<Home> { inclusive = true } }
                        }
                    }) { Text("Sair") }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.Quotes,
                    onClick = { onTabSelected(MainTab.Quotes) },
                    icon = { Text("📈") },
                    label = { Text("Cotações") },
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.Portfolio,
                    onClick = { onTabSelected(MainTab.Portfolio) },
                    icon = { Text("💼") },
                    label = { Text("Carteira") },
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                MainTab.Quotes -> AssetQuotesScreen(
                    quotesViewModel = quotesViewModel,
                    fiisViewModel = fiisViewModel,
                    onViewStockValuation = { onOpenValuation(it.toAssetValuationInput()) },
                    onViewFiiValuation = { onOpenValuation(it.toAssetValuationInput()) },
                )
                MainTab.Portfolio -> {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onAddOrder = {
                            orderFormViewModel.reset()
                            showQuickOrder = true
                        },
                        onImport = { navController.navigate(Import) },
                        onViewValuation = {
                            val state = dashboardState
                            if (state is DashboardUiState.Success) {
                                onOpenValuationList(state.summary.positions.map { it.toAssetValuationInput() })
                            }
                        },
                    )
                }
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