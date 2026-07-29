package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterScreen
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import com.danilobarreto.stockapp.quotes.data.QuotesApiClient
import com.danilobarreto.stockapp.quotes.data.QuotesRepositoryImpl
import com.danilobarreto.stockapp.quotes.presentation.QuoteScreen
import com.danilobarreto.stockapp.quotes.presentation.QuotesViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.danilobarreto.stockapp.portfolio.data.PortfolioRepositoryImpl
import com.danilobarreto.stockapp.portfolio.data.PositionsApiClient
import com.danilobarreto.stockapp.portfolio.presentation.DashboardScreen
import com.danilobarreto.stockapp.portfolio.presentation.DashboardViewModel
import com.danilobarreto.stockapp.quotes.data.FiisApiClient
import com.danilobarreto.stockapp.quotes.data.FiisRepositoryImpl
import com.danilobarreto.stockapp.quotes.presentation.AssetQuotesScreen
import com.danilobarreto.stockapp.quotes.presentation.FiisViewModel

private enum class MainTab { Quotes, Portfolio }

@Composable
@Preview
fun App() {
    val tokenStorage = remember { TokenStorage() }
    val httpClient = remember { createAppHttpClient(tokenStorage) }

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient,appBaseUrl()), tokenStorage)
    }

    val quotesRepository = remember {
        QuotesRepositoryImpl(QuotesApiClient(baseUrl = appBaseUrl(), httpClient = httpClient))
    }

    val portfolioRepository = remember {
        PortfolioRepositoryImpl(PositionsApiClient(httpClient, appBaseUrl()))
    }

    val fiisRepository = remember {
        FiisRepositoryImpl(FiisApiClient(baseUrl = appBaseUrl(), httpClient = httpClient))
    }

    val loginViewModel = remember { LoginViewModel(authRepository) }
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val quotesViewModel = remember { QuotesViewModel(quotesRepository) }
    val dashboardViewModel = remember { DashboardViewModel(portfolioRepository) }
    val fiisViewModel = remember { FiisViewModel(fiisRepository) }

    val navController = rememberNavController()
    val startDestination = if(authRepository.isLoggedIn.value) Home else Login

    StockAppTheme {
        NavHost(navController = navController, startDestination = startDestination){
            composable<Login>{
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Home){
                            popUpTo<Login>{ inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Register) }
                )
            }
            composable<Register>{
                RegisterScreen(
                    viewModel = registerViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Home){
                            popUpTo<Login> { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable<Home> {
                var selectedTab by remember { mutableStateOf(MainTab.Quotes) }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == MainTab.Quotes,
                                onClick = { selectedTab = MainTab.Quotes },
                                icon = { Text("📈") },
                                label = { Text("Cotações") },
                            )
                            NavigationBarItem(
                                selected = selectedTab == MainTab.Portfolio,
                                onClick = { selectedTab = MainTab.Portfolio },
                                icon = { Text("💼") },
                                label = { Text("Carteira") },
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (selectedTab) {
                            MainTab.Quotes -> {
                                AssetQuotesScreen(quotesViewModel, fiisViewModel)
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            authRepository.logout()
                                            navController.navigate(Login) {
                                                popUpTo<Home> { inclusive = true }
                                            }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd).safeContentPadding().padding(16.dp)
                                ) { Text("Sair") }
                            }
                            MainTab.Portfolio -> {
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}