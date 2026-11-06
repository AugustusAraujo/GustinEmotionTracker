package com.example.gustinemotiontracker.core.model

/**
 * Os 4 quadrantes do Modelo Circunplexo de Afeto (Russell):
 * dois eixos — ativação (energia) e valência (agradável/desagradável).
 */
enum class Quadrant(val label: String, val colorArgb: Long) {
    HIGH_ENERGY_PLEASANT(
        label = "Alta Energia / Agradável",
        colorArgb = 0xFFFFB300
    ),
    HIGH_ENERGY_UNPLEASANT(
        label = "Alta Energia / Desagradável",
        colorArgb = 0xFFE53935
    ),
    LOW_ENERGY_PLEASANT(
        label = "Baixa Energia / Agradável",
        colorArgb = 0xFF43A047
    ),
    LOW_ENERGY_UNPLEASANT(
        label = "Baixa Energia / Desagradável",
        colorArgb = 0xFF5C6BC0
    )
}
