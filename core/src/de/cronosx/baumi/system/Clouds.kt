package de.cronosx.baumi.system

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

class Clouds(var batch: Batch) : IteratingSystem(
        allOf(Cloud::class, Position::class).get()) {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()
    val textureCloud = Texture("cloud.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val cloud = clouds.get(entity)
        /* val region = TextureRegion(textureCloud, 0, 12 * cloud.index, 120, 12) */
        batch.draw(textureCloud, position.x, position.y)
        if (position.x + textureCloud.width < 0) {
            engine.removeEntity(entity)
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        val cloudCount = engine.entities.filter { clouds.has(it) }.count()
        if (cloudCount < 2) {
            info { "Only $cloudCount clouds alive. Spawning cloud." }
            engine.add { entity {
                with<Position> {
                    position = vec2(
                        appWidth + Math.random().toFloat() * textureCloud.width,
                        appHeight - 10 - Math.random().toFloat() * textureCloud.height
                    )
                }
                with<Cloud> {
                    index = Math.floor(Math.random() * 3f).toInt()
                }
                with<Movable> {
                    weight = 0.3f
                }
            } }
        }
    }
}
