package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import kotlinx.serialization.*

@Serializable
class Buffer(
    var max: Float = 0f,
    var current: Float = 0f,
    var energyYield: Float = 0f
) : Component
