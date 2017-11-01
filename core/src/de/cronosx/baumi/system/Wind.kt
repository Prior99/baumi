package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Wind : IteratingSystem(
        allOf(Movable::class, Position::class).get()) {
    val movables = mapperFor<Movable>()
    val positions = mapperFor<Position>()

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val movable = movables.get(entity)
        if (position.y <= config.groundHeight || movable.fixed) {
            return
        }
        position.x += (delta * world.windDirection) / (movable.weight / 100f)
    }

    override fun update(delta: Float) {
        super.update(delta)
        world.windDirection += delta * Math.random().toFloat() * 0.3f
        world.windDirection = minOf(world.windDirection, 10f)
        world.windDirection = maxOf(world.windDirection, -10f)
    }
}
