package de.cronosx.baumi.test

import com.badlogic.gdx.Screen
import ktx.app.KtxGame

class EmptyApplication : KtxGame<Screen>() {
    override fun create () { }
    override fun dispose () { }
    override fun render() { }
}

