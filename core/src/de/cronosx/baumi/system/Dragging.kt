package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*

data class CurrentTarget(
        val entity: Entity,
        val offsetToCursor: Vector2
)

class Dragging() : EntitySystem() {
    val positions = mapperFor<Position>()
    val draggables = mapperFor<Draggable>()

    var current: CurrentTarget? = null

    fun touchDown(touchPosition: Vector2) {
        val targetEntity = engine.entities
                .filter{ draggables.has(it) && positions.has(it) }
                .find{
                    val position = positions.get(it).position.cpy()
                    val draggable = draggables.get(it)
                    val size = draggable.size.cpy()
                    val offset = draggable.offset.cpy()
                    touchPosition + offset >= position && touchPosition + offset <= position + size
                }
        if (targetEntity != null) {
            // Reset time.
            val entityPosition = positions.get(targetEntity).position
            // Store entity and the offset to the cursor.
            current = CurrentTarget(
                    targetEntity,
                    offsetToCursor = touchPosition - entityPosition
            )
        }
    }

    fun touchUp() {
        if (current != null) {
            current = null
        }
    }

    fun touchDragged(touchPosition: Vector2) {
        if (current == null) {
            return
        }
        val position = positions.get(current!!.entity)
        val actualPosition = touchPosition - current!!.offsetToCursor
        position.position = vec2(actualPosition.x, maxOf(actualPosition.y, config.cloudHeight))
    }
    override fun update(delta: Float) {
        // Cloud could have been deleted.
        if (current != null && (!draggables.has(current!!.entity) || !positions.has(current!!.entity))) {
            current = null
        }
    }
}

