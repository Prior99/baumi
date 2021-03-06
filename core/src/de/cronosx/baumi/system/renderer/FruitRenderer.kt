package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.component.*
import de.cronosx.baumi.Math.*
import ktx.ashley.mapperFor
import ktx.log.*

class FruitRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val fruits = mapperFor<Fruit>()
    val positions = mapperFor<Position>()
    val genetics = mapperFor<Genetic>()

    val texture = Texture("blossom.png")
    val textureHeight = 50
    val textureWidth = 50
    val bloomIndex = 9
    val fruitIndex = 29

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val fruit = fruits.get(entity)
        val geneFruit = genetics.get(entity).dna.fruits

        val growingStartAge = 0
        val bloomingStartAge = growingStartAge + geneFruit.growingDuration
        val fruitStartAge = bloomingStartAge + geneFruit.bloomingDuration
        val fruitEndAge = fruitStartAge + geneFruit.fruitDuration
        val age = fruit.age

        // Calculate x and y offset in texture.
        val x = textureWidth * when {
            age < bloomingStartAge -> bloomIndex * age.toFloat() / geneFruit.growingDuration.toFloat()
            age < fruitStartAge -> bloomIndex.toFloat()
            age < fruitEndAge -> bloomIndex.toFloat() +
                    (fruitIndex - bloomIndex) * (age.toFloat() - fruitStartAge) / geneFruit.fruitDuration
            else -> fruitIndex.toFloat()
        }.toInt()
        // Create sprite and render it.
        val cropped = TextureRegion(texture, x, 0, textureWidth, textureHeight)
        val sprite = Sprite(cropped)
        // Rotate the fruit randomly.
        sprite.setOrigin(textureWidth / 2f, textureHeight / 2f)
        sprite.rotation = radiansToDegrees * fruit.rotation
        // Render at the given position
        sprite.setPosition(position.x, position.y)
        sprite.setScale(0.5f +
                if (age > fruitStartAge)
                    ((minOf(age, fruitEndAge) - fruitStartAge).toFloat() / geneFruit.fruitDuration) * 0.5f
                else 0f
        )
        sprite.translate(-textureWidth / 2f, -textureHeight / 2f)
        sprite.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter {
            fruits.has(it) && positions.has(it)
        }
        entities.forEach { processEntity(it, delta) }
    }
}
