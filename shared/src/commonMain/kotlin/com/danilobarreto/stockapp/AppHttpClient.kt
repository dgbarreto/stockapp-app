package com.danilobarreto.stockapp

import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.data.createAuthenticatedHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin

fun createAppHttpClient(tokenStorage: TokenStorage): HttpClient {
    val client = createAuthenticatedHttpClient(appBaseUrl(), tokenStorage)

    client.plugin(HttpSend).intercept { request ->
        val call = execute(request)
        println("STOCKAPP_HTTP → ${request.method.value} ${request.url.buildString()} → ${call.response.status.value}")
        call
    }

    return client
}