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
import ktx.log.*
import de.cronosx.baumi.system.*
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val textureBackground = Texture("background.png")
    val engine = PooledEngine()

    override fun show() {
        engine.addSystem(SimpleRenderer(batch))
        engine.addSystem(Clouds(batch))
        engine.addSystem(Tree(defaultDna, 10f))
        engine.addSystem(BranchRenderer(batch))
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
        batch.begin()
        batch.draw(textureBackground, 0f, 0f)
        engine.update(delta)
        batch.end()
    }

    override fun hide() {
    
    }
}
