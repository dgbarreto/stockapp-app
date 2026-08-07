package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
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

@Composable
@Preview
fun App() {
    val tokenStorage = remember { TokenStorage() }
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
    val orderFormViewModel = remember { OrderFormViewModel(ordersRepository) }
    val importViewModel = remember { ImportViewModel(importRepository) }

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
            importViewModel = importViewModel,
        )
    }
}