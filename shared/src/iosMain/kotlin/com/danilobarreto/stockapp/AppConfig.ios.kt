package com.danilobarreto.stockapp

// O simulador iOS compartilha a rede da máquina host, então localhost funciona direto
// (diferente do emulador Android, que roda numa rede isolada).
actual fun appBaseUrl(): String = "http://localhost:3000"
