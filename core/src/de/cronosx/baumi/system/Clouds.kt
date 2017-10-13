package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Clouds() : EntitySystem() {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()
    val textureCloud = Texture("cloud.png")

    override fun update(delta: Float) {
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
