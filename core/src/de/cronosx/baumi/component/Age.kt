package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import kotlinx.serialization.*

@Serializable
class Age(
    var age: Int = 0
) : Component
