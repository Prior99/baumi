package de.cronosx.baumi.data

/**
 * This gene solely controls the initial rotation of the tree's root.
 */
data class GeneRotation (
    val initial: Float
)

/**
 * Controls all values related to leafs.
 */
data class GeneLeafs (
    // The amount of leafs per length on the maximum generation.
    // This value defines how dense the leafs on the max generations should grow.
    // The density for all other branches is deducted from this value.
    // If the value is for example `0.01` and the current length is `300`,
    // 3 leafs would grow.
    val maxGenerationLeafCountPerLength: Float,
    // This value influences how much the amount of leafs is reduced per generation.
    // It is multiplied with the maximum amount of leafs of any children.
    // So if the value is `0.5` and the children are nested 4 levels deep then the
    // branche's maxiumum amount of leafs will be `0.5^4` times smaller than the one
    // at the maximum generation defined in `maxGenerationLeafCountPerLength`.
    val leafCountFalloff: Float,
    // The maximum offset of the rotation of a leaf in the range of 0 to 1 with 0 being 0deg
    // and 1 being 90deg.
    val maxRotationOffset: Float,
    // The amount of energy needed for each tick.
    val upkeep: Float,
    // The cost of energy for creating a new leaf.
    val leafCost: Float,
    // The maximum amount of energy a leaf can store.
    val maxEnergy: Float,
    // The maximum amount of health one lead can have.
    val maxHealth: Float,
    val maxYoungLeafs: Int
)

/**
 * This gene controls all values influencing the length of branches in a tree.
 */
data class GeneLength (
    // The speed with which a branch can grow per tick. The length (in pixels) will be
    // increased by this value each tick.
    val growthSpeed: Float,
    // The maximum length of a branch in pixels. This value is taken for the trunk
    // (root branch) of the tree and all other branches deduct their maximum from this value
    // by multiplying it with `falloff`.
    val max: Float,
    // The initial length of the trunk (root branch) when the tree is first spawned.
    val initial: Float,
    // The factor with which the child branches will be smaller than their parents.
    // if this value is for example `0.5` and the parent's maximum length was `100` then the
    // child will be of length `50`.
    val falloff: Float,
    // The cost of energy for growing in length.
    val growCost: Float
)

/**
 * Controls all values responsible for the tree's health.
 */
data class GeneHealth (
    // The maximum health of a branch.
    val max: Float,
    // The falloff with which the maximum health of a child branch will be deducted from
    // the parent's maximum health.
    // If this value is for example `0.2` and the parent had a max health of `10`, then the
    // child's maximum health will be `2`.
    val falloff: Float
)

/**
 * This gene control the way energy is handled within a tree.
 */
data class GeneEnergy (
    // The maximum amount of energy a branch can store.
    val max: Float,
    // This value is used to deduct the maximum energy **and upkeep rate** for a child branch from
    // it's parents maximum energy. If the parent's maximum energy was for example
    // `40` and this value is `0.25`, then the children of that branch will have
    // a maximum energy capacity of `10`.
    val falloff: Float,
    // The amount of energy needed to survive each tick.
    val upkeep: Float,
    // The minimum amount of energy to keep in storage and which will not be used to grow in any way.
    val minKeep: Float
)

/**
 * This gene influences the way a branch spawns new child branches.
 */
data class GeneBranching (
    // The maximum amount of levels the tree will have.
    //    \/         4
    //     \/        3
    //      \/ \/    2
    //       \ /     1
    //        |      0
    val maxDepth: Int,
    // The minimum length a branch must have before being able to branch.
    val minLength: Float,
    // The probability that a branch will not spawn two but three child branches.
    val tripleProbability: Float,
    // The cost of energy for branching.
    val branchCost: Float,
    // The radius to keep between two branches from 0 to 1 with 0 being 0deg and 1 being
    // the maximum amount of spacing without getting ridiculous (90deg for triple and 180deg
    // for tuple).
    val rotationSpacing: Float,
    // The variety for the rotation with 0 being 0deg and 1 being 180deg.
    val rotationVariety: Float
)

data class GeneFruits (
    val maxValue: Int,
    val maxGenerationFruitCountPerLength: Float,
    val upkeep: Float,
    val maxEnergy: Float,
    val fruitCost: Float,
    val growingDuration: Int,
    val bloomingDuration: Int,
    val fruitDuration: Int,
    val minGeneration: Int
)

data class DNA (
    val rotation: GeneRotation,
    val leafs: GeneLeafs,
    val length: GeneLength,
    val health: GeneHealth,
    val energy: GeneEnergy,
    val branching: GeneBranching,
    val fruits: GeneFruits
)

val defaultDna = DNA(
    rotation = GeneRotation(
        initial = Math.PI.toFloat() / 2f
    ),
    leafs = GeneLeafs(
        maxGenerationLeafCountPerLength = 0.5f,
        leafCountFalloff = 0.2f,
        maxRotationOffset = 0.2f,
        upkeep = 0.05f,
        leafCost = 20f,
        maxEnergy = 0.1f,
        maxHealth = 0.3f,
        maxYoungLeafs = 3
    ),
    length = GeneLength(
        growthSpeed = 0.1f,
        max = 400f,
        initial = 100f,
        falloff = 0.6f,
        growCost = 1f
    ),
    health = GeneHealth(
        max= 10f,
        falloff = 0.6f
    ),
    energy = GeneEnergy(
        max = 100f,
        falloff = 0.6f,
        upkeep = 0.5f,
        minKeep = 6f
    ),
    branching = GeneBranching(
        maxDepth = 4,
        minLength = 0.3f,
        tripleProbability = 0.6f,
        branchCost = 20f,
        rotationSpacing = 0.2f,
        rotationVariety = 0.2f
    ),
    fruits = GeneFruits(
        maxValue = 100,
        maxGenerationFruitCountPerLength = 0.02f,
        minGeneration = 1,
        upkeep = 0.01f,
        maxEnergy = 1f,
        fruitCost = 25f,
        growingDuration = 300,
        bloomingDuration = 1000,
        fruitDuration = 200
    )
)
