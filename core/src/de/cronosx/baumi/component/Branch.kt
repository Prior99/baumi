package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*

class Branch(
        var rotation: Float = 0f,
        var length: Float = 0f,
        var maxLength: Float = 0f,
        var generation: Int = 0,
        var leafProbability: Float = 0f,
        var children: MutableList<Entity> = ArrayList()
) : Component

