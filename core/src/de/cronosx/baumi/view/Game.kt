package de.cronosx.baumi.view

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.math.*
import de.cronosx.baumi.system.SimpleRenderer

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val textureBackground = Texture("background.png")
    val textureCloud = Texture("cloud.png")
    val view = table {
        setFillParent(true)
        background = TextureRegionDrawable(TextureRegion(textureBackground, 0, 0, 135, 240))
        pack()
    }
    val engine = PooledEngine()

    override fun show() {
        stage.addActor(view)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
    
    }
}
