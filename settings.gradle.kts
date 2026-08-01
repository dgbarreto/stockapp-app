rootProject.name = "StockApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val localProperties = java.util.Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val useLocalQuotes = localProperties.getProperty("useLocalQuotes", "false").toBoolean()
val useLocalAuth = localProperties.getProperty("useLocalAuth", "false").toBoolean()
val useLocalPortfolio = localProperties.getProperty("useLocalPortfolio", "false").toBoolean()
val useLocalOrders = localProperties.getProperty("useLocalOrders", "false").toBoolean()

fun prop(name: String): String? =
    System.getenv(name) ?: localProperties.getProperty(name)

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            name = "GitHubPackagesDesignSystem"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-designsystem")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesQuotes"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-quotes")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesAuth"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-auth")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesPortfolio"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-portfolio")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesOrders"
            url = uri("https://maven.pkg.github.com/dgbarreto/stockapp-orders")
            credentials {
                username = prop("GITHUB_ACTOR")
                password = prop("GITHUB_TOKEN")
            }
        }
        mavenLocal()
    }
}

include(":androidApp")
include(":shared")

if (useLocalQuotes) {
    includeBuild("../stockapp-quotes") {
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:quotes"))
                .using(project(":quotes"))
        }
    }
}
if (useLocalAuth) {
    includeBuild("../stockapp-auth") {
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:auth"))
                .using(project(":auth"))
        }
    }
}
if (useLocalPortfolio) {
    includeBuild("../stockapp-portfolio") {
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:portfolio"))
                .using(project(":portfolio"))
        }
    }
}
if (useLocalOrders) {
    includeBuild("../stockapp-orders") {
        dependencySubstitution {
            substitute(module("com.danilobarreto.stockapp:orders"))
                .using(project(":orders"))
        }
    }
}