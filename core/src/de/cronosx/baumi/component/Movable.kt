package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Movable(
    var weight: Float = 1f,
    var floating: Boolean = true
) : Component
