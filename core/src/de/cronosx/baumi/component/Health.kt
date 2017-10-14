package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import de.cronosx.baumi.data.*
import kotlinx.serialization.*

@Serializable
class Health(
    var max: Float = 0f,
    var current: Float = 0f
) : Component {
    val alive: Boolean
        get() = current > 0f

    fun kill() {
        current = 0f
    }
}
