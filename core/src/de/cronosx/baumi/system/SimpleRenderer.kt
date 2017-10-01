package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import de.cronosx.baumi.util.*
import com.artemis.Aspect
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch

@Wire(failOnNull = false)
class SimpleRenderer(var batch: Batch) : IteratingSystem(
        allOf(SimpleDrawable::class, Position::class, ZIndex::class)) {
    val cSimpleDrawable by require<SimpleDrawable>()
    val cPosition by require<Position>()
    val cZ by require<ZIndex>()

    override fun begin() {
        batch.begin()
    }

    override fun process(entityId: Int) {
        val position = cPosition.get(entityId).position
        val drawable = cSimpleDrawable.get(entityId)
        val z = cZ.get(entityId)
        batch.draw(drawable.texture, position.x, position.y, drawable.size.x, drawable.size.y)
    }

    override fun end() {
        batch.end()
    }
}
