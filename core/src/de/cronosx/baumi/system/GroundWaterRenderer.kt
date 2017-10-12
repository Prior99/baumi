package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import ktx.ashley.*
import ktx.math.*
import ktx.log.*
import de.cronosx.baumi.Math.*

class GroundWaterRenderer(var batch: Batch) : EntitySystem() {
    val buffers = mapperFor<Buffer>()
    val groundWaters = mapperFor<GroundWater>()
    val textureGroundWater = Texture("ground-water.png")
    val frames = 10
    val textureHeight = 360
    val textureWidth = 1080

    override fun update(delta: Float) {
        super.update(delta)
        val entities = engine.entities.filter { buffers.has(it) && groundWaters.has(it) }
        if (entities.count() != 1) {
            error { "Invalid amount of entities with component `GroundWater` found." }
            return
        }
        val buffer = buffers.get(entities[0])
        val x = minOf(10, Math.round(frames * (buffer.current / buffer.max).toDouble()).toInt())
        val texture = TextureRegion(textureGroundWater, x * textureWidth, 0, textureWidth, textureHeight)
        val sprite = Sprite(texture)
        sprite.setPosition(0f, 0f)
        sprite.draw(batch)
    }
}
