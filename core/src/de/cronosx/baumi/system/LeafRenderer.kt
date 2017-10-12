package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.log.*

class LeafRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Leaf::class, Position::class).get(), ZComparator()) {
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    val healths = mapperFor<Health>()
    val leafTexture = Texture("leaf.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val leaf = leafs.get(entity)
        val health = healths.get(entity)

        if (health.alive) {
            val col = Math.floor(((1 - health.current / health.max) * 8f).toDouble()).toInt()
            val region = TextureRegion(leafTexture, 100 * col, 60, 100, 60)
            val sprite = Sprite(region)
            sprite.setOrigin(0f, 30f)
            sprite.rotation = radiansToDegrees * leaf.rotation
            sprite.setPosition(position.x, position.y + 30 - 10)
            /* info { "Leaf health ${health.current} / ${health.max}" } */
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
