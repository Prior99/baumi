package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Events() : EntitySystem() {
    val buffers = mapperFor<Buffer>()
    val groundWaters = mapperFor<GroundWater>()

    fun wateringCan() {
        val entities = engine.entities.filter{ groundWaters.has(it) && buffers.has(it) }
        for (entity in entities) {
            val buffer = buffers.get(entity)
            buffer.current = minOf(buffer.max, buffer.current + world.wateringCanCapacity)
        }
    }
}
