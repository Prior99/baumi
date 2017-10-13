package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine

abstract class RenderSubSystem(val engine: Engine) {
    abstract fun render(delta: Float)
}
