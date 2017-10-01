package de.cronosx.baumi.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import ktx.scene2d.*

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val view = table {
        setFillParent(true)
        pack()
    }

    override fun show() {
    
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
    
    }
}
