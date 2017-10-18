package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*
import ktx.math.*

class Rain() : EntitySystem() {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()

    val currentCloud: Entity? = null
    val size = vec2(500f, 250f)

    fun touchDown(x: Int, y: Int) {
        val touchPosition = vec2(x.toFloat(), y.toFloat())
        engine.entities
            .filter{ clouds.has(it) && positions.has(it) }
            .find{
                val position = positions.get(it)
                return touchPosition >= position && touchPosition <= position + size
            }
    }

    fun touchUp() {
        if (currentCloud != null) {
            currentCloud = null
        }
    }

    override fun update(delta: Float) {

    }
}
