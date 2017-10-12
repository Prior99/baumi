package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Leaf(
    var rotation: Float = 0f,
    var generation: Int = 0,
    var positionAlongBranch: Float = 1f
) : Component
