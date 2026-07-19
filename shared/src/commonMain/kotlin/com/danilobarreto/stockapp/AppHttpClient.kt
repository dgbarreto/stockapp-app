package com.danilobarreto.stockapp

import com.danilobarreto.stockapp.auth.data.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.handleCoroutineException
import kotlinx.serialization.json.Json

fun createAppHttpClient(tokenStorage: TokenStorage): HttpClient =
    HttpClient {
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