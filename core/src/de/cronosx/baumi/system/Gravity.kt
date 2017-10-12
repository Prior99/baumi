package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
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
        if (position.y > world.groundHeight && !movable.floating) {
            position.y -= movable.weight * delta
        }
    }
}
