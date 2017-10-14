package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import kotlinx.serialization.*

@Serializable
class Decompose(
    var current: Float = 0f,
    var max: Float = 0f,
    var speed: Float = 0f
) : Component
