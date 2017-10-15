package de.cronosx.baumi.data

data class Config (
    val groundHeight: Float,
    val initialWater: Float,
    val maxWater: Float,
    val waterEnergyYield: Float,
    val wateringCanCapacity: Float,
    val tickSpeed: Float,
    val serializationInterval: Float
)

val config = Config(
    groundHeight = 360f,
    initialWater = 180000f,
    maxWater = 360000f,
    waterEnergyYield = 5f,
    wateringCanCapacity = 10000f,
    tickSpeed = 2f,
    serializationInterval = 10f
)
