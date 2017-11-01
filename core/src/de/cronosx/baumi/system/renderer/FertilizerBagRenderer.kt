package de.cronosx.baumi.system.renderer

import ktx.ashley.mapperFor
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.component.FertilizerBag
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.data.config


class FertilizerBagRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val fertilizerBags = mapperFor<FertilizerBag>()
    val positions = mapperFor<Position>()

    val bagTexture = Texture("fertilizer.png")
    val textureHeight = 51
    val textureWidth = 195
    val frames = 5

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val fertilizerSack = fertilizerBags.get(entity)
        // Calculate x and y offset in texture.
        val x = textureWidth * ((1f - fertilizerSack.content.toFloat() / config.maxFertilizerContent) * frames).toInt()
        // Create sprite and render it.
        val texture = TextureRegion(bagTexture, x, 0, textureWidth, textureHeight)
        val sprite = Sprite(texture)
        sprite.setPosition(position.x, position.y)
        sprite.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter {
            fertilizerBags.has(it) && positions.has(it)
        }
        entities.forEach { processEntity(it, delta) }
    }
}
