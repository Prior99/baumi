package de.cronosx.baumi.data

data class World (
    val groundHeight: Float,
    val initialWater: Float,
    val maxWater: Float,
    val waterEnergyYield: Float
)

val world = World(
    groundHeight = 320f,
    initialWater = 400f,
    maxWater = 500f,
    waterEnergyYield = 1f
)
