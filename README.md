# stockapp-app

Módulo KMP "compositor" do [StockApp](https://github.com/dgbarreto/stockapp-app) — app de acompanhamento de investimentos (cotações, carteira, ordens, indicadores fundamentalistas e calculadora de preço justo), 100% Kotlin Multiplatform + Compose Multiplatform, Android e iOS. Projeto de estudo (Kotlin/KMP/Compose Multiplatform, GitHub Actions, GCP, Kubernetes).

Agrega os módulos de feature (`stockapp-designsystem`, `stockapp-quotes`, `stockapp-portfolio`, `stockapp-orders`, `stockapp-valuation` — repos separados) num `NavHost` comum.

## Estrutura

- `androidApp/` — app Android fino, gera o APK diretamente (`com.android.application`), só consome `shared`.
- `shared/` — módulo KMP com o código comum (NavHost, telas), alvo Android (lib) + iOS (framework estático `Shared`).
- `iosApp/` — projeto Xcode mínimo, único ponto não-Kotlin do projeto (SwiftUI só hospeda o `ComposeUIViewController`).

## Status

**Fase 1 — Fundação**: scaffold criado via [KMP Wizard](https://kmp.jetbrains.com/) da JetBrains, NavHost ainda vazio.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Rodando

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: abrir `/iosApp` no Xcode
- Testes: `./gradlew :shared:testAndroidHostTest` · `./gradlew :shared:iosSimulatorArm64Test`

---

_Progresso mantido manualmente conforme o projeto avança._
