package de.cronosx.baumi.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Texture
import com.badlogic.ashley.core.Component
import ktx.math.*
import ktx.ashley.*

class SimpleDrawable(
        var texture: Texture = Texture("missing-texture.png"),
        var size: Vector2 = vec2(0f, 0f)
) : Component
