package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*
import de.cronosx.baumi.data.*

class Health(
    var max: Float = 0f,
    var current: Float = 0f
) : Component {
    fun dead(): Boolean {
        return current <= 0
    }
}

