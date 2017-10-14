package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import kotlinx.serialization.*

@Serializable
class Producer(
    var rate: Float = 0f
) : Component
