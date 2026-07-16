package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.danilobarreto.stockapp.quotes.data.QuotesApiClient
import com.danilobarreto.stockapp.quotes.data.QuotesRepositoryImpl
import com.danilobarreto.stockapp.quotes.presentation.QuoteScreen
import com.danilobarreto.stockapp.quotes.presentation.QuotesViewModel

@Composable
@Preview
fun App() {
    val viewModel = remember {
        val apiClient = QuotesApiClient(baseUrl = appBaseUrl())
        val repository = QuotesRepositoryImpl(apiClient)
        QuotesViewModel(repository)
    }
    QuoteScreen(viewModel)
}