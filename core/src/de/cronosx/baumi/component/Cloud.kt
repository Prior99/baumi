package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Cloud(
        var speed: Float = 30f,
        var index: Int = 0
) : Component
