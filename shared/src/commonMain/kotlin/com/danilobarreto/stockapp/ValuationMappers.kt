package com.danilobarreto.stockapp

import com.danilobarreto.stockapp.portfolio.domain.PositionSummary
import com.danilobarreto.stockapp.quotes.domain.Fii
import com.danilobarreto.stockapp.quotes.domain.QuoteFundamentals
import com.danilobarreto.stockapp.valuation.domain.AssetFundamentals
import com.danilobarreto.stockapp.valuation.domain.AssetValuationInput

// Ponte entre stockapp-portfolio e stockapp-valuation — nenhum dos dois módulos
// conhece o outro (ver docs/decisoes.md do repo de planejamento); quem traduz
// um domínio pro outro é sempre o composition root (stockapp-app).
fun PositionSummary.toAssetValuationInput(): AssetValuationInput = AssetValuationInput(
    ticker = ticker,
    name = null, // /positions/summary não devolve nome da empresa/fundo, só ticker
    fundamentals = AssetFundamentals(
        currentPrice = currentPrice,
        eps = eps,
        bookValuePerShare = bookValuePerShare,
        dividendPerShareTtm = dividendPerShareTtm,
        dividendYieldTtm = null, // /positions/summary não calcula yield, só valor por ação
        earningsCagr5y = earningsCagr5y?.div(100), // backend manda percentual (77.68); valuation espera fração (0.7768)
        priceToSalesRatio = priceToSalesRatio,
    ),
)

// Idem, pra entrada individual vinda da aba Cotações (ação).
fun QuoteFundamentals.toAssetValuationInput(): AssetValuationInput = AssetValuationInput(
    ticker = ticker,
    name = null, // /quotes/:ticker não devolve nome da empresa
    fundamentals = AssetFundamentals(
        currentPrice = closePrice,
        eps = lpa,
        bookValuePerShare = vpa,
        dividendPerShareTtm = dividendPerShareTtm,
        dividendYieldTtm = dividendPerShareTtm?.let { it / closePrice }, // bolsai não manda yield de ação pronto; calculado aqui, já em fração
        earningsCagr5y = earningsCagr5y?.div(100),
        priceToSalesRatio = priceToSalesRatio,
    ),
)

// Idem, pra entrada individual vinda da aba Cotações (FII).
fun Fii.toAssetValuationInput(): AssetValuationInput = AssetValuationInput(
    ticker = ticker,
    name = name,
    fundamentals = AssetFundamentals(
        currentPrice = closePrice,
        eps = null, // Graham não se aplica a FII (não existe LPA de fundo)
        bookValuePerShare = bookValuePerShare,
        dividendPerShareTtm = dividendPerShareTtm,
        dividendYieldTtm = dividendYieldTtm?.div(100), // bolsai manda percentual (8.5 = "8.5%"); valuation espera fração
        earningsCagr5y = distributionGrowthRate?.div(100), // growth de distribuição faz o papel do "g" pra FII
        priceToSalesRatio = null, // P/S não se aplica a FII
    ),
)