package de.cronosx.baumi.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.ashley.core.Component
import ktx.math.*
import ktx.ashley.*
import kotlinx.serialization.*

@Serializable
class Position(
    var position: Vector2 = vec2(0f, 0f)
) : Component
