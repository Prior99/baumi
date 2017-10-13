package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.component.*
import de.cronosx.baumi.Math.*
import ktx.log.*

class Renderer(var batch: Batch) : EntitySystem() {
    val textureBackground = Texture("background.png")
    val textureGrass = Texture("grass.png")
    var subSystems: List<RenderSubSystem> = ArrayList()

    override fun addedToEngine(engine: Engine) {
        subSystems = listOf(
            BranchRenderer(batch, engine),
            CloudRenderer(batch, engine),
            GroundWaterRenderer(batch, engine),
            LeafRenderer(batch, engine),
            FruitRenderer(batch, engine)
        )
    }

    override fun update(delta: Float) {
        batch.begin()
        batch.draw(textureBackground, 0f, 0f)
        for (system in subSystems) {
            system.render(delta)
        }
        batch.draw(textureGrass, 0f, 0f)
        batch.end()
    }
}
