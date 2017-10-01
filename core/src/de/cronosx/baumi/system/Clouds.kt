package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Clouds : IteratingSystem(
        allOf(Cloud::class, Position::class).get()) {
    val cCloud = mapperFor<Cloud>()
    val cPosition = mapperFor<Position>()
    val textureCloud = Texture("cloud.png")
    val cloudSpeed = 30f

    override fun processEntity(entity: Entity, delta: Float) {
        val position = cPosition.get(entity).position
        position.x -= cloudSpeed * delta
        if (position.x + textureCloud.width < 0) {
            engine.removeEntity(entity)
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        val cloudCount = engine.entities.filter{ cCloud.has(it) }.count()
        if (cloudCount < 2) {
            info { "Only $cloudCount clouds alive. Spawning cloud." }
            engine.add{entity {
                with<Position> {
                    position = vec2(
                        appWidth + Math.random().toFloat() * textureCloud.width,
                        appHeight - textureCloud.height - Math.random().toFloat() * textureCloud.height
                    )
                }
                with<SimpleDrawable> { texture = textureCloud }
                with<ZIndex> { z = -1000 }
                with<Cloud> {}
            }}
        }
    }
}
