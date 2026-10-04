package com.danilobarreto.stockapp

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_BALANCE_VISIBLE = "ui_balance_visible"

/** Preferências de interface deste aparelho (não seguem a conta). */
class UiPreferences(private val settings: Settings = Settings()) {

    private val _balanceVisible = MutableStateFlow(settings.getBoolean(KEY_BALANCE_VISIBLE, true))
    val balanceVisible: StateFlow<Boolean> = _balanceVisible.asStateFlow()

    fun setBalanceVisible(visible: Boolean) {
        settings.putBoolean(KEY_BALANCE_VISIBLE, visible)
        _balanceVisible.value = visible
    }

    fun toggleBalanceVisible() = setBalanceVisible(!_balanceVisible.value)
}