package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import de.cronosx.baumi.component.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class CloudRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()
    val textureCloud = Texture("cloud.png")

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val cloud = clouds.get(entity)
        batch.draw(textureCloud, position.x, position.y)
        if (position.x + textureCloud.width < 0) {
            engine.removeEntity(entity)
        }
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter{ clouds.has(it) && positions.has(it) }
        entities.forEach{ processEntity(it, delta) }
    }
}
