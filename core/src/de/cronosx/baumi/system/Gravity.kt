package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Gravity() : IteratingSystem(
        allOf(Movable::class, Position::class).get()) {
    val movables = mapperFor<Movable>()
    val positions = mapperFor<Position>()

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val movable = movables.get(entity)
        if (position.y > world.groundHeight && !movable.floating && !movable.fixed) {
            position.y -= movable.weight * delta
        }
    }
}
