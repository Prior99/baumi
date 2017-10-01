package baumi

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxGame
import ktx.inject.Context
import ktx.async.enableKtxCoroutines
import baumi.view.Game

class Application : KtxGame<Screen>() {
    val context = Context()

    override fun create () {
        enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton<Viewport>(ScreenViewport())
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
