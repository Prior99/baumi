package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import kotlinx.serialization.*

@Serializable
class Cloud(
    var index: Int = 0
) : Component
