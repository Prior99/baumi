package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*
import de.cronosx.baumi.data.*

class Consumer(
    var maxEnergy: Float = 0f,
    var energy: Float = 0f,
    var rate: Float = 0f,
    var priority: Float = 0f
) : Component {
}
