package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.data.debug
import de.cronosx.baumi.component.*
import de.cronosx.baumi.system.tick.Ticker
import de.cronosx.baumi.Math.*
import ktx.log.*

class Renderer(var batch: Batch, val ticker: Ticker) : EntitySystem() {
    val textureBackground = Texture("background.png")
    val textureGrass = Texture("grass.png")
    var subSystems: List<RenderSubSystem> = ArrayList()

    override fun addedToEngine(engine: Engine) {
        subSystems = listOf(
            BranchRenderer(batch, engine),
            RainDropRenderer(batch, engine),
            CloudRenderer(batch, engine),
            GroundWaterRenderer(batch, engine),
            LeafRenderer(batch, engine),
            FruitRenderer(batch, engine),
            LoadingRenderer(batch, engine, ticker)
        )
    }

    override fun update(delta: Float) {
        if (debug.disableRendering) return
        batch.begin()
        batch.draw(textureBackground, 0f, 0f)
        for (system in subSystems) {
            system.render(delta)
        }
        batch.draw(textureGrass, 0f, 0f)
        batch.end()
    }
}
