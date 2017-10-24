package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import de.cronosx.baumi.Bus.on
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.config
import de.cronosx.baumi.events.Drag
import de.cronosx.baumi.events.DragStart
import de.cronosx.baumi.events.DragStop
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.log.info

class CartSystem() : IteratingSystem(allOf(Position::class, Cart::class).get()) {
    val positions = mapperFor<Position>()
    val carts = mapperFor<Cart>()
    var dragging = false

    val maxAngle = 0.06f * FloatMath.PI
    var deltaY = 0f

    init {
        on { event: Drag ->
            if (positions.has(event.entity) && carts.has(event.entity)) {
                val position = positions.get(event.entity).position
                position.y = config.groundHeight
                val cart = carts.get(event.entity)
                if (cart.angle >= 0.0f) {
                    position.x -= event.delta.x
                }
                deltaY += event.delta.y
                deltaY = FloatMath.clamp(deltaY, -300f, 300f)
                cart.angle = FloatMath.clamp(FloatMath.asin(-deltaY / 300f), -maxAngle, maxAngle)
            }
        }
        on { _event: DragStart ->
            dragging = true
            deltaY = 0f
        }
        on { _event: DragStop ->
            dragging = false
        }
    }

    override fun processEntity(entity: Entity?, delta: Float) {
        if (!dragging) {
            val cart = carts.get(entity)
            if (cart.angle < maxAngle) {
                cart.angle += delta * FloatMath.PI
            }
        }
    }
}

