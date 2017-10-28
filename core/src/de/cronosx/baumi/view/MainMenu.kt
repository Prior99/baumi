package de.cronosx.baumi.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import de.cronosx.baumi.Application
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*

val whiteBackground = {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.WHITE)
    pixmap.fill()
    TextureRegionDrawable(TextureRegion(Texture(pixmap)))
}()

class MainMenu(val stage: Stage, val batch: Batch, val application: Application) : KtxScreen {
    val view = table {
        setFillParent(true)
        background = whiteBackground
        button {
            label("Game")
            onClick {
                application.setScreen<Game>()
            }
        }
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }
}

