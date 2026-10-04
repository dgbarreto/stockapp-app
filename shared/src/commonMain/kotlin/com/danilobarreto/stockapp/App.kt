package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.auth.presentation.PasswordResetViewModel
import com.danilobarreto.stockapp.auth.presentation.RegisterViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import com.danilobarreto.stockapp.imports.data.ImportApiClient
import com.danilobarreto.stockapp.imports.data.ImportRepositoryImpl
import com.danilobarreto.stockapp.imports.presentation.ImportViewModel
import com.danilobarreto.stockapp.orders.data.OrdersApiClient
import com.danilobarreto.stockapp.orders.data.OrdersRepositoryImpl
import com.danilobarreto.stockapp.orders.presentation.OrderFormViewModel
import com.danilobarreto.stockapp.portfolio.data.PortfolioRepositoryImpl
import com.danilobarreto.stockapp.portfolio.data.PositionsApiClient
import com.danilobarreto.stockapp.portfolio.presentation.DashboardViewModel
import com.danilobarreto.stockapp.quotes.data.FiisApiClient
import com.danilobarreto.stockapp.quotes.data.FiisRepositoryImpl
import com.danilobarreto.stockapp.quotes.data.QuotesApiClient
import com.danilobarreto.stockapp.quotes.data.QuotesRepositoryImpl
import com.danilobarreto.stockapp.quotes.presentation.FiisViewModel
import com.danilobarreto.stockapp.quotes.presentation.QuotesViewModel
import com.danilobarreto.stockapp.auth.presentation.ProfileViewModel
import com.danilobarreto.stockapp.portfolio.presentation.HomeViewModel
import com.danilobarreto.stockapp.quotes.presentation.QuoteDetailViewModel
import com.danilobarreto.stockapp.quotes.presentation.FiiDetailViewModel
import com.danilobarreto.stockapp.orders.domain.AssetType as OrderAssetType

@Composable
@Preview
fun App() {
    val tokenStorage = remember { TokenStorage() }
    val uiPreferences = remember { UiPreferences() }
    val httpClient = remember { createAppHttpClient(tokenStorage) }

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient, appBaseUrl()), tokenStorage)
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
    val ordersRepository = remember {
        OrdersRepositoryImpl(OrdersApiClient(httpClient, appBaseUrl()))
    }
    val importRepository = remember {
        ImportRepositoryImpl(ImportApiClient(httpClient, appBaseUrl()))
    }

    val loginViewModel = remember { LoginViewModel(authRepository) }
    val registerViewModel = remember { RegisterViewModel(authRepository) }
    val quotesViewModel = remember { QuotesViewModel(quotesRepository) }
    val dashboardViewModel = remember { DashboardViewModel(portfolioRepository) }
    val fiisViewModel = remember { FiisViewModel(fiisRepository) }
    val orderFormViewModel = remember {
        OrderFormViewModel(
            repository = ordersRepository,
            priceLookup = { ticker, type ->
                when (type) {
                    OrderAssetType.FII -> fiisRepository.getFii(ticker).closePrice
                    OrderAssetType.STOCK -> quotesRepository.getFundamentals(ticker).closePrice
                }
            },
        )
    }
    val importViewModel = remember { ImportViewModel(importRepository) }
    val passwordResetViewModel = remember { PasswordResetViewModel(authRepository) }
    val homeViewModel = remember { HomeViewModel(portfolioRepository) }
    val profileViewModel = remember { ProfileViewModel(authRepository) }
    val quoteDetailViewModel = remember { QuoteDetailViewModel(quotesRepository) }
    val fiiDetailViewModel = remember { FiiDetailViewModel(fiisRepository) }

    val navController = rememberNavController()
    val startDestination = if (authRepository.isLoggedIn.value) Home else Login

    StockAppTheme {
        AppNavHost(
            navController = navController,
            startDestination = startDestination,
            authRepository = authRepository,
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            quotesViewModel = quotesViewModel,
            fiisViewModel = fiisViewModel,
            dashboardViewModel = dashboardViewModel,
            orderFormViewModel = orderFormViewModel,
            ordersRepository = ordersRepository,
            importViewModel = importViewModel,
            passwordResetViewModel = passwordResetViewModel,
            homeViewModel = homeViewModel,
            profileViewModel = profileViewModel,
            quoteDetailViewModel = quoteDetailViewModel,
            fiiDetailViewModel = fiiDetailViewModel,
            uiPreferences = uiPreferences
        )
    }
}