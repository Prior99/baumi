package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.component.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class LeafRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Leaf::class, Position::class, Age::class).get(), ZComparator()) {
    val leafs = mapperFor<Leaf>()
    val ages = mapperFor<Age>()
    val positions = mapperFor<Position>()
    val healths = mapperFor<Health>()
    val leafTexture = Texture("leaf.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val leaf = leafs.get(entity)
        val age = ages.get(entity)
        val health = healths.get(entity)

        if (health.alive) {
            val x = 100 * minOf(age.age / 10, 10)
            val sprite = Sprite(TextureRegion(leafTexture, x, 0, 100, 60))
            sprite.setOrigin(0f, leafTexture.height / 2f)
            sprite.rotation = radiansToDegrees * leaf.rotation
            sprite.setPosition(position.x, position.y + leafTexture.height / 2f - 10)
            sprite.setScale(0.3f)
            sprite.draw(batch)
        }
    }

    class ZComparator : Comparator<Entity> {
        val leafs = mapperFor<Leaf>()

        override fun compare(e1: Entity, e2: Entity): Int {
            return leafs.get(e1).generation - leafs.get(e2).generation
        }
    }
}
