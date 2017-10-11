package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Death() : IteratingSystem(
        allOf(Consumer::class, Health::class).get()) {
    val consumers = mapperFor<Consumer>()
    val healths = mapperFor<Health>()
    val branches = mapperFor<Branch>()

    override fun processEntity(entity: Entity, delta: Float) {
        val consumer = consumers.get(entity)
        val health = healths.get(entity)

        if (consumer.energy <= 0f && health.alive) {
            killRecursively(entity)
        }
    }

    fun killRecursively(entity: Entity) {
        info { "Killing entity recursively." }
        val health = healths.get(entity)
        health.kill()
        val branch = branches.get(entity)
        if (branch != null) {
            branch.children.forEach { killRecursively(it) }
        }
    }
}
