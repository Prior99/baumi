package de.cronosx.baumi.data

data class World (
    val groundHeight: Float,
    val initialWater: Float,
    val maxWater: Float,
    val waterEnergyYield: Float,
    val wateringCanCapacity: Float,
    val tickSpeed: Float
)

val world = World(
    groundHeight = 320f,
    initialWater = 400f,
    maxWater = 500f,
    waterEnergyYield = 8f,
    wateringCanCapacity = 50f,
    tickSpeed = 0.25f
)
