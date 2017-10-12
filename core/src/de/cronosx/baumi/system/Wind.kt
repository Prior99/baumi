package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class Wind() : IteratingSystem(
        allOf(Movable::class, Position::class).get()) {
    val movables = mapperFor<Movable>()
    val positions = mapperFor<Position>()
    var direction = Math.random().toFloat() * 20f - 10f

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val movable = movables.get(entity)
        if (position.y < world.groundHeight) {
            return
        }
        position.x += (delta * direction) / (movable.weight / 100f)
    }

    override fun update(delta: Float) {
        super.update(delta)
        direction += delta * Math.random().toFloat() * 0.3f
        direction = minOf(direction, 10f)
        direction = maxOf(direction, 10f)
    }
}
