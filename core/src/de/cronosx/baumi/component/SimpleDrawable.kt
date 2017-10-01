package de.cronosx.baumi.component

import com.artemis.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Texture
import ktx.math.*

abstract class SimpleDrawable : Component() {
    abstract val texture: Texture
    abstract var size: Vector2
}
