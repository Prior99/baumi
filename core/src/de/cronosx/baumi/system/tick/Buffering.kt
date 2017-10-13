package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Buffering(engine: Engine) : TickSubSystem(engine) {
    val buffers = mapperFor<Buffer>()
    val producers = mapperFor<Producer>()

    init {
        // Ground water.
        engine.entity {
            with<Producer> {
                rate = 0f
            }
            with<Buffer> {
                max = world.maxWater
                current = world.initialWater
                energyYield = world.waterEnergyYield
            }
            with<GroundWater> {}
        }
    }

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!buffers.has(entity) || !producers.has(entity)) {
                continue
            }
            val buffer = buffers.get(entity)
            val producer = producers.get(entity)
            if (buffer.current > 0f) {
                producer.rate = maxOf(buffer.energyYield, buffer.current)
                buffer.current = maxOf(0f, buffer.current - buffer.energyYield)
            } else {
                producer.rate = 0f
            }
        }
    }
}
