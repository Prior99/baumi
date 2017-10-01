package de.cronosx.baumi.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Texture
import ktx.math.*
import com.badlogic.ashley.core.Component
import ktx.ashley.*

abstract class SimpleDrawable : Component {
    abstract val texture: Texture
    abstract var size: Vector2
}
