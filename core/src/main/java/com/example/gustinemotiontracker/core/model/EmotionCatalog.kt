package com.example.gustinemotiontracker.core.model

/**
 * Catálogo de emoções específicas por quadrante, para granularidade emocional.
 */
object EmotionCatalog {

    val emotionsByQuadrant: Map<Quadrant, List<String>> = mapOf(
        Quadrant.HIGH_ENERGY_PLEASANT to listOf(
            "Animado", "Eufórico", "Enérgico", "Confiante", "Motivado"
        ),
        Quadrant.HIGH_ENERGY_UNPLEASANT to listOf(
            "Ansioso", "Estressado", "Irritado", "Frustrado", "Sobrecarregado"
        ),
        Quadrant.LOW_ENERGY_PLEASANT to listOf(
            "Calmo", "Tranquilo", "Grato", "Satisfeito", "Relaxado"
        ),
        Quadrant.LOW_ENERGY_UNPLEASANT to listOf(
            "Triste", "Desmotivado", "Cansado", "Vazio", "Melancólico"
        )
    )

    fun emotionsFor(quadrant: Quadrant): List<String> =
        emotionsByQuadrant.getValue(quadrant)
}
