package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Decomposing(engine: Engine) : TickSubSystem(engine) {
    val decomposes = mapperFor<Decompose>()
    val healths = mapperFor<Health>()

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!decomposes.has(entity) || !healths.has(entity)) {
                continue;
            }
            if (healths.get(entity).alive) {
                continue;
            }
            val decompose = decomposes.get(entity)
            if (decompose.current < decompose.max) {
                decompose.current += decompose.speed
            }
        }
    }
}
