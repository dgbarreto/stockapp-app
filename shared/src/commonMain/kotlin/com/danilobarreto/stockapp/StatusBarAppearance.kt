package com.danilobarreto.stockapp

import androidx.compose.runtime.Composable

/**
 * Ajusta a cor dos ícones da status bar (relógio/bateria/wifi) conforme o
 * fundo da tela atual. `useLightIcons = true` = ícones brancos (fundo escuro,
 * como o header framboesa da Início); `false` = ícones escuros (fundo claro).
 */
@Composable
expect fun SetStatusBarAppearance(useLightIcons: Boolean)