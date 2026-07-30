# stockapp-app

The "composer" KMP module of StockApp — an investment tracking app (quotes, portfolio, orders, fundamental indicators and a fair-price calculator), 100% Kotlin Multiplatform + Compose Multiplatform, Android and iOS. Learning project (Kotlin/KMP/Compose Multiplatform, GitHub Actions, GCP, Kubernetes).

Aggregates the feature modules ([`stockapp-designsystem`](https://github.com/dgbarreto/stockapp-designsystem), [`stockapp-auth`](https://github.com/dgbarreto/stockapp-auth), [`stockapp-quotes`](https://github.com/dgbarreto/stockapp-quotes), [`stockapp-portfolio`](https://github.com/dgbarreto/stockapp-portfolio), [`stockapp-orders`](https://github.com/dgbarreto/stockapp-orders) — separate repos, `stockapp-valuation` still to come) into a single `NavHost`.

## Structure

- `androidApp/` — thin Android app, generates the APK directly (`com.android.application`), only consumes `shared`.
- `shared/` — the KMP module with the shared code (composition root, `NavHost`, shared HTTP client), targeting Android (lib) + iOS (static framework `Shared`).
- `iosApp/` — minimal Xcode project, the only non-Kotlin part of the whole project (SwiftUI just hosts the `ComposeUIViewController`).

## What's in it

- `AppHttpClient.kt` — a single Ktor `HttpClient`, shared across every feature module, with the `Auth`/`bearer` plugin reading the JWT from `stockapp-auth`'s `TokenStorage`. No module besides `auth` itself needs to know about authentication.
- `Routes.kt`/`App.kt` — the composition root: a `NavHost` with `Login`/`Register` routes, a `Home` route (`Scaffold` + Material3 `NavigationBar` toggling the **Quotes** and **Portfolio** tabs) and `AddPosition` as a stacked route.

## Status

Fully wired end-to-end: sign up/login (`stockapp-auth`) → `Home` with bottom navigation between Quotes (`stockapp-quotes`) and Portfolio (`stockapp-portfolio`) → add a position → logout. Android app builds and runs for real; the iOS shell (`stockapp-app-ios-shell`) is a later roadmap phase, not built yet.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1 · Navigation Compose 2.9.2 · Ktor Client

## Running

- Android: `./gradlew :androidApp:assembleDebug`
- iOS: open `/iosApp` in Xcode
- Tests: `./gradlew :shared:testAndroidHostTest` · `./gradlew :shared:iosSimulatorArm64Test`

---

_Progress kept up to date manually as the project moves forward._
