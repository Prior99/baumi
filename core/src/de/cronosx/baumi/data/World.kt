package de.cronosx.baumi.data

import kotlinx.serialization.*

@Serializable
data class World (
    var tick: Int,
    var lastTick: Double,
    var windDirection: Float
)

val world = World(
    tick = 0,
    lastTick = System.currentTimeMillis().toDouble() / 1000.0,
    windDirection = Math.random().toFloat() * 20f - 10f
)
