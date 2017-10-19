package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Decomposing(engine: Engine) : TickSubSystem(engine) {
    val decomposes = mapperFor<Decompose>()
    val healths = mapperFor<Health>()

    override fun tick(number: Int) {
        engine.entities
                .filter { decomposes.has(it) && healths.has(it) && !healths.get(it).alive }
                .map { decomposes.get(it) }
                .filter { it.current < it.max }
                .forEach { it.current += it.speed }
    }
}
