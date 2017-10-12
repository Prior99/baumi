package de.cronosx.baumi.view

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.Texture
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.math.*
import ktx.log.*
import de.cronosx.baumi.system.*
import de.cronosx.baumi.system.tick.Ticker
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val textureBackground = Texture("background.png")
    val textureGrass = Texture("grass.png")
    val engine = PooledEngine()

    override fun show() {
        engine.addSystem(Clouds(batch))
        engine.addSystem(Ticker())
        engine.addSystem(BranchRenderer(batch))
        engine.addSystem(LeafRenderer(batch))
        engine.addSystem(Gravity())
        engine.addSystem(Wind())
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
        batch.begin()
        batch.draw(textureBackground, 0f, 0f)
        engine.update(delta)
        batch.draw(textureGrass, 0f, 0f)
        batch.end()
    }

    override fun hide() {

    }
}
