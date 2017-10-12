package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Aging(engine: Engine) : TickSubSystem(engine) {
    val ages = mapperFor<Age>()

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!ages.has(entity)) {
                continue;
            }
            val age = ages.get(entity)
            age.age++
        }
    }
}
