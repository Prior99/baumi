package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
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

class FruitRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val fruits = mapperFor<Fruit>()
    val ages = mapperFor<Age>()
    val positions = mapperFor<Position>()
    val genetics = mapperFor<Genetic>()

    val texture = Texture("blossom.png")
    val textureHeight = 50
    val textureWidth = 50
    val bloomIndex = 13
    val fruitIndex = 26

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val fruit = fruits.get(entity)
        val age = ages.get(entity).age
        val geneFruit = genetics.get(fruit.parent).dna.fruits

        val growingStartAge = 0
        val bloomingStartAge = growingStartAge + geneFruit.growingDuration
        val fruitStartAge = bloomingStartAge + geneFruit.growingDuration

        // Calculate x and y offset in texture.
        val x = textureWidth * (
            if (age < bloomingStartAge) bloomIndex * age.toFloat() / geneFruit.growingDuration.toFloat()
            else if (age < fruitStartAge) bloomIndex.toFloat()
            else bloomIndex.toFloat() +
                (fruitIndex - bloomIndex) * (age.toFloat() - fruitStartAge).toFloat() / geneFruit.fruitDuration
        ).toInt()
        // Create sprite and render it.
        val cropped = TextureRegion(texture, x, 0, textureWidth, textureHeight)
        val sprite = Sprite(cropped)
        sprite.setOrigin(textureWidth.toFloat() / 2f, textureHeight.toFloat() / 2f)
        sprite.rotation = radiansToDegrees * fruit.rotation
        sprite.setPosition(position.x, position.y)
        sprite.setScale(0.3f)
        sprite.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter{
            fruits.has(it) && positions.has(it) && ages.has(it)
        }
        entities.forEach{ processEntity(it, delta) }
    }
}
