package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class CloudRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()

    val textureCloud = Texture("cloud.png")
    val textureWidth = 500
    val textureHeight = 250
    val frames = 10

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val cloud = clouds.get(entity)
        val x = (frames.toFloat() * cloud.content / config.maxCloudContent).toInt() * textureWidth
        val region = TextureRegion(textureCloud, x, 0, textureWidth, textureHeight)
        batch.draw(region, position.x, position.y)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { clouds.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}
