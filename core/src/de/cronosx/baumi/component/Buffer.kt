package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Buffer(
    var max: Float = 0f,
    var current: Float = 0f
) : Component
