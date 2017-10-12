package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.component.*
import de.cronosx.baumi.Math.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import ktx.log.*

class LeafRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Leaf::class, Position::class, Age::class, Decompose::class).get(), ZComparator()) {
    val leafs = mapperFor<Leaf>()
    val ages = mapperFor<Age>()
    val decomposes = mapperFor<Decompose>()
    val positions = mapperFor<Position>()
    val healths = mapperFor<Health>()

    val leafTexture = Texture("leaf.png")
    val textureHeight = 60
    val textureWidth = 100

    override fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val leaf = leafs.get(entity)
        val age = ages.get(entity).age
        val decompose = decomposes.get(entity)
        val alive = healths.get(entity).alive
        // Calculate x and y offset in texture.
        val x = textureWidth * (
            if (alive) minOf(age / 10, 7) else FloatMath.floor(decompose.current).toInt()
        )
        val y = textureHeight * if (alive) 0 else 1
        // Create sprite and render it.
        val texture = TextureRegion(leafTexture, x, y, textureWidth, textureHeight)
        val sprite = Sprite(texture)
        sprite.setOrigin(0f, textureHeight.toFloat() / 2f)
        sprite.rotation = radiansToDegrees * leaf.rotation
        sprite.setPosition(position.x, position.y + textureHeight.toFloat() / 2f - 10f)
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
