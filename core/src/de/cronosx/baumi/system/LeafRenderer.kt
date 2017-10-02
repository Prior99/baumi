package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.Leaf
import de.cronosx.baumi.component.Position
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.log.info

class LeafRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Leaf::class, Position::class).get(), ZComparator()) {
    val cLeaf = mapperFor<Leaf>()
    val cPosition = mapperFor<Position>()
    val leafTexture = Texture("leaf.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = cPosition.get(entity).position
        val leaf = cLeaf.get(entity)
        val sprite = Sprite(leafTexture)
        sprite.setOrigin(0f, leafTexture.height / 2f)
        sprite.rotation = radiansToDegrees * leaf.rotation
        sprite.setPosition(position.x, position.y)
        sprite.draw(batch)
    }

    class ZComparator : Comparator<Entity> {
        val cLeaf = mapperFor<Leaf>()

        override fun compare(e1: Entity, e2: Entity): Int {
            return cLeaf.get(e1).generation - cLeaf.get(e2).generation;
        }
    }
}
