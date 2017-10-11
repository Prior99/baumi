package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import de.cronosx.baumi.component.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class LeafRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Leaf::class, Position::class).get(), ZComparator()) {
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    val healths = mapperFor<Health>()
    val leafTexture = Texture("leaf.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val leaf = leafs.get(entity)
        val dead = healths.get(entity).dead
        if (dead) {
            return
        }
        val sprite = Sprite(leafTexture)
        sprite.setOrigin(0f, leafTexture.height / 2f)
        sprite.rotation = radiansToDegrees * leaf.rotation
        sprite.setPosition(position.x, position.y + leafTexture.height / 2f - 10)
        sprite.setScale(0.3f)
        sprite.draw(batch)
    }

    class ZComparator : Comparator<Entity> {
        val leafs = mapperFor<Leaf>()

        override fun compare(e1: Entity, e2: Entity): Int {
            return leafs.get(e1).generation - leafs.get(e2).generation
        }
    }
}
