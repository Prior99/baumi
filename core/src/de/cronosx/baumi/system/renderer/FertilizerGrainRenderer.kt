package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.Buffer
import de.cronosx.baumi.component.FertilizerBag
import de.cronosx.baumi.component.FertilizerGrain
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.data.config
import ktx.ashley.mapperFor

class FertilizerGrainRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val fertilizerGrains = mapperFor<FertilizerGrain>()
    val positions = mapperFor<Position>()
    val buffers = mapperFor<Buffer>()

    val leafTexture = Texture("fertilizer-grain.png")
    val textureHeight = 8
    val textureWidth = 8

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val buffer = buffers.get(entity)
        val fertilizerGrain = fertilizerGrains.get(entity)
        // Calculate x and y offset in texture.
        val x = textureWidth * fertilizerGrain.index
        // Create sprite and render it.
        val texture = TextureRegion(leafTexture, x, 0, textureWidth, textureHeight)
        val sprite = Sprite(texture)
        sprite.setPosition(position.x, position.y)
        sprite.rotation = fertilizerGrain.rotation
        sprite.setOriginCenter()
        sprite.setAlpha(buffer.current.toFloat() / buffer.max)
        sprite.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter {
            fertilizerGrains.has(it) && positions.has(it)
        }
        entities.forEach { processEntity(it, delta) }
    }
}