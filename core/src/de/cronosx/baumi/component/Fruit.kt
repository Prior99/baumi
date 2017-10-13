package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*

class Fruit(
    var rotation: Float = 0f,
    var generation: Int = 0,
    var positionAlongBranch: Float = 1f,
    var parent: Entity? = null,
    var age: Int = 0
) : Component
