package com.danilobarreto.stockapp

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Register

@Serializable
object Home

@Serializable
object Import

@Serializable
object ValuationList

@Serializable
data class Valuation(val ticker: String)

@Serializable
object ForgotPassword

@Serializable
object ResetCode

@Serializable
object NewPassword

@Serializable
object Profile