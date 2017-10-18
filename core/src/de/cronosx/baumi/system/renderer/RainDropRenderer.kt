package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.RainDrop
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.data.config
import ktx.ashley.mapperFor

class RainDropRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val rainDrops = mapperFor<RainDrop>()
    val positions = mapperFor<Position>()

    val textureRainDrop = Texture("drop.png")
    val textureWidth = 2
    val textureHeight = 10
    val frames = 4

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val rainDrop = rainDrops.get(entity)
        val x = rainDrop.index * textureWidth
        val region = TextureRegion(textureRainDrop, x, 0, textureWidth, textureHeight)
        batch.draw(region, position.x, position.y)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { rainDrops.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}