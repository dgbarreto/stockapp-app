package com.danilobarreto.stockapp

import com.danilobarreto.stockapp.auth.data.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.handleCoroutineException
import kotlinx.serialization.json.Json
import io.ktor.http.HttpHeaders

fun createAppHttpClient(tokenStorage: TokenStorage): HttpClient {
    val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation){
            json(Json { ignoreUnknownKeys = true })
        }
        install(Auth){
            bearer {
                loadTokens {
                    tokenStorage.read()?.let { BearerTokens(it, refreshToken = "") }
                }
            }
        }
    }

    client.plugin(HttpSend).intercept { request ->
        println("STOCKAPP_HTTP → ${request.method.value} ${request.url.buildString()}")
        println("STOCKAPP_HTTP → Authorization: ${request.headers[HttpHeaders.Authorization]}")
        execute(request)
    }

    return client
}