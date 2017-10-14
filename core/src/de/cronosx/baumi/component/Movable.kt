package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import kotlinx.serialization.*

@Serializable
class Movable(
    var weight: Float = 1f,
    var floating: Boolean = true,
    var fixed: Boolean = true
) : Component
