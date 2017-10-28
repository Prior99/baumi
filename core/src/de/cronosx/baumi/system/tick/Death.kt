package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Death(engine: Engine) : TickSubSystem(engine) {
    val healths = mapperFor<Health>()
    val branches = mapperFor<Branch>()
    val parents = mapperFor<Parent>()

    fun killRecursively(entity: Entity) {
        val health = healths.get(entity)
        health.kill()
        val parent = parents.get(entity)
        parent?.children?.forEach { killRecursively(it) }
    }

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!healths.has(entity)) {
                continue
            }
            val health = healths.get(entity)
            if (!health.alive) {
                killRecursively(entity)
            }
        }
    }
}
