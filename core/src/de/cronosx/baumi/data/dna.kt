package de.cronosx.baumi.data

val defaultDna = DNA(
    rotation = Math.PI.toFloat() / 2f,
    perGenerationBranchLengthFactor = 0.6f,
    growthSpeed = 0.1f,
    leafGrowProbability = 0.1f,
    maxLeafCount = 5,
    minLengthGenerationThreshold = 0.3f,
    generateProbability = 0.3f,
    handToChildrenProbability = 0.4f,
    tripleProbability = 0.6f,
    maxLength = 400f,
    maxGeneration = 5,
    maxLeafRotationOffset = 0.2f,
    initialSize = 100f
)

data class DNA (
    val rotation: Float,
    val perGenerationBranchLengthFactor: Float,
    val growthSpeed: Float,
    val leafGrowProbability: Float,
    val maxLeafCount: Int,
    val minLengthGenerationThreshold: Float,
    val generateProbability: Float,
    val handToChildrenProbability: Float,
    val tripleProbability: Float,
    val maxLength: Float,
    val maxGeneration: Int,
    val maxLeafRotationOffset: Float,
    val initialSize: Float
)
