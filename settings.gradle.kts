rootProject.name = "StockApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val localProperties = java.util.Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val useLocalQuotes = localProperties.getProperty("useLocalQuotes", "false").toBoolean()

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