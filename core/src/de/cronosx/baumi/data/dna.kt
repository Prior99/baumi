package de.cronosx.baumi.data

val defaultDna = DNA(
        maxBranchLength = 50f,
        rotation = Math.PI.toFloat() / 2f,
        perGenerationBranchLengthFactor = 0.6f,
        growthSpeed = 1f
)

data class DNA (
    val maxBranchLength: Float,
    val rotation: Float,
    val perGenerationBranchLengthFactor: Float,
    val growthSpeed: Float
)
