package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import de.cronosx.baumi.data.*
import kotlinx.serialization.*

@Serializable
class Consumer(
    var maxEnergy: Float = 0f,
    var minEnergy: Float = 0f,
    var energy: Float = 0f,
    var rate: Float = 0f,
    var effectiveness: Float = 1f,
    var healthDecayRate: Float = 0.001f
) : Component {
    val remainingBufferCapacity: Float
        get() = maxEnergy - energy
}
