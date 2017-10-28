package de.cronosx.baumi.data

class Version(
    val major: Int,
    val minor: Int,
    val patch: Int
) {
    constructor(version: String) : this(
            version.split(".").getOrElse(0, { "0" }).toInt(),
            version.split(".").getOrElse(1, { "0" }).toInt(),
            version.split(".").getOrElse(2, { "0" }).toInt()) {}
    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    fun isCompatible(other: Version): Boolean {
        return other.major == major;
    }
}

data class Config (
    val groundHeight: Float,
    val cloudHeight: Float,
    val initialWater: Float,
    val maxWater: Float,
    val waterEnergyYield: Float,
    val wateringCanCapacity: Float,
    val tickSpeed: Float,
    val serializationInterval: Float,
    val maxTicksPerInterval: Int,
    val maxCloudContent: Float,
    val minCloudContent: Float,
    val clouds: Int,
    val dropContent: Float,
    val dropsPerSecond: Float,
    val maxCartContent: Int,
    val maxFertilizerContent: Int,
    val maxFertilizerBuffer: Float,
    val fertilizerYield: Float,
    val fertilizerCost: Int,
    val maxReplayTicks: Int,
    val version: Version
)

val config = Config(
    groundHeight = 360f,
    cloudHeight = 500f,
    initialWater = 1000f,
    maxWater = 360000f,
    waterEnergyYield = 15f,
    wateringCanCapacity = 10000f,
    tickSpeed = 2f,
    serializationInterval = 10f,
    maxTicksPerInterval = 100,
    maxCloudContent = 20000f,
    minCloudContent = 2000f,
    clouds = 4,
    dropContent = 50f,
    dropsPerSecond = 5f,
    maxCartContent = 35,
    maxFertilizerContent = 12,
    maxFertilizerBuffer = 70000f,
    fertilizerYield = 0.5f,
    fertilizerCost = 5,
    maxReplayTicks = 20000,
    version = Version(3, 0, 0)
)
