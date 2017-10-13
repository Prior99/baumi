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
    groundHeight = 360f,
    initialWater = 100f,
    maxWater = 1000f,
    waterEnergyYield = 8f,
    wateringCanCapacity = 50f,
    tickSpeed = 0.5f
)
