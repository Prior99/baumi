package baumi.system

import baumi.component.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.ashley.core.Family.Builder
import com.badlogic.ashley.core.Entity
import ktx.ashley.*

class SimpleRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(SimpleDrawable::class, Position::class, ZIndex::class).get(), ZComparator()) {
    val cSimpleDrawable = mapperFor<SimpleDrawable>()
    val cPosition = mapperFor<Position>()

    override fun processEntity(entityId: Entity, delta: Float) {
        val position = cPosition.get(entityId).position
        val drawable = cSimpleDrawable.get(entityId)
        batch.begin()
        batch.draw(drawable.texture, position.x, position.y, drawable.size.x, drawable.size.y)
        batch.end()
    }

    class ZComparator : Comparator<Entity> {
        val cZ = mapperFor<ZIndex>()

        override fun compare(e1: Entity, e2: Entity): Int {
            return cZ.get(e1).z - cZ.get(e2).z;
        }
    }
}
