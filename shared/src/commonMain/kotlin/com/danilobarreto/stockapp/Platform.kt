package com.danilobarreto.stockapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform