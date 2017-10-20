package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import de.cronosx.baumi.system.renderer.CloudRenderer
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.data.*
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Clouds() : EntitySystem() {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()

    val textureWidth = 500
    val textureHeight = 250

    override fun update(delta: Float) {
        val cloudCount = engine.entities.filter { clouds.has(it) }.count()
        val doSpawn = Math.random().toFloat() < 1f * delta * (1f / cloudCount.toFloat())
        if (cloudCount < config.clouds && doSpawn) {
            val x = if (world.windDirection < 0) appWidth.toFloat() else -textureWidth.toFloat()
            val y = appHeight.toFloat() - textureHeight.toFloat() - Math.random().toFloat() * textureHeight.toFloat()
            debug { "Only $cloudCount/${config.clouds} clouds alive. Spawning cloud at $x/$y." }
            engine.entity {
                with<Position> {
                    position = vec2(x, y)
                }
                with<Cloud> {
                    content = config.minCloudContent +
                            (config.maxCloudContent - config.minCloudContent) * Math.random().toFloat()
                }
                with<Movable> {
                    weight = Math.random().toFloat() * 4f + 4f
                    floating = true
                    fixed = false
                }
                with<Uuid> {}
            }
        }
        for (entity in engine.entities) {
            if (!clouds.has(entity)) {
                continue
            }
            val position = positions.get(entity).position
            val cloud = clouds.get(entity)
            val remove = position.x + textureWidth < -appWidth ||
                    position.x > appWidth * 2 ||
                    cloud.content <= 0f
            if (remove) {
                engine.removeEntity(entity)
            }
        }
    }
}
