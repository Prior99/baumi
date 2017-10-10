package de.cronosx.baumi.data

data class GeneRotation (
    val initial: Float
)

data class GeneLeafs (
    val leafGrowProbability: Float,
    val max: Int,
    val maxRotationOffset: Float,
    val upkeep: Float
)

data class GeneLength (
    val growthSpeed: Float,
    val max: Float,
    val initial: Float,
    val falloff: Float,
    val growCost: Float
)

data class GeneHealth (
    val max: Float,
    val falloff: Float
)

data class GeneEnergy (
    val max: Float,
    val falloff: Float,
    val upkeep: Float,
    val minKeep: Float
)

data class GeneBranching (
    val maxDepth: Int,
    val minLength: Float,
    val tripleProbability: Float,
    val probability: Float,
    val branchCost: Float
)

data class DNA (
    val rotation: GeneRotation,
    val leafs: GeneLeafs,
    val length: GeneLength,
    val health: GeneHealth,
    val energy: GeneEnergy,
    val branching: GeneBranching
)

val defaultDna = DNA(
    rotation = GeneRotation(
        initial = Math.PI.toFloat() / 2f
    ),
    leafs = GeneLeafs(
        leafGrowProbability = 0.1f,
        max = 5,
        maxRotationOffset = 0.2f,
        upkeep = 0.01f
    ),
    length = GeneLength(
        growthSpeed = 0.1f,
        max= 50f,
        initial= 10f,
        falloff = 0.6f,
        growCost = 1f
    ),
    health = GeneHealth(
        max= 10f,
        falloff = 0.6f
    ),
    energy = GeneEnergy(
        max = 10f,
        falloff = 0.6f,
        upkeep = 0.5f,
        minKeep = 6f
    ),
    branching = GeneBranching(
        maxDepth = 4,
        minLength = 0.3f,
        tripleProbability = 0.6f,
        probability = 0.3f,
        branchCost = 2f
    )
)
