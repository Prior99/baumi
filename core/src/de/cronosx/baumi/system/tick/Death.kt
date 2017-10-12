package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Death(engine: Engine) : TickSubSystem(engine) {
    val consumers = mapperFor<Consumer>()
    val healths = mapperFor<Health>()
    val branches = mapperFor<Branch>()

    fun killRecursively(entity: Entity) {
        info { "Killing entity recursively." }
        val health = healths.get(entity)
        health.kill()
        val branch = branches.get(entity)
        if (branch != null) {
            branch.children.forEach { killRecursively(it) }
        }
    }

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!consumers.has(entity) || !healths.has(entity)) {
                continue
            }
            val consumer = consumers.get(entity)
            val health = healths.get(entity)
            if (consumer.energy <= 0f && health.alive) {
                killRecursively(entity)
            }
        }
    }
}
