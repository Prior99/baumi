package de.cronosx.baumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxGame
import ktx.inject.Context
import ktx.async.enableKtxCoroutines
import de.cronosx.baumi.view.Game

val appWidth = 135f
val appHeight = 240f

fun getScale() = Gdx.graphics.width.toFloat() / appWidth

class Application : KtxGame<Screen>() {
    val context = Context()

    override fun create () {
        enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            val viewport = FitViewport(appWidth, appHeight)
            bindSingleton<Viewport>(viewport)
            bindSingleton(Stage(inject(), inject()))
            bindSingleton(this@Application)
            bindSingleton(Game(inject(), inject()))
        }
        addScreen(context.inject<Game>())
        setScreen<Game>()
    }

    override fun dispose () {
        context.dispose()
    }
}
