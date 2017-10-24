package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.component.Cart
import de.cronosx.baumi.component.Position
import ktx.ashley.mapperFor
import ktx.math.vec2

class CartRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val carts = mapperFor<Cart>()
    val positions = mapperFor<Position>()

    val textureWheel = Texture("cart/wheel.png")
    val textureCart = Texture("cart/cart.png")
    val textureFruits = Texture("cart/fruits.png")
    val cartWidth = 325
    val cartHeight = 125
    val wheelWidth = 86
    val fruitsWidth = 234
    val fruitsHeight = 114
    val wheelLength = FloatMath.PI * 2f * 43
    val rotationCenter = vec2(256f, 42f)

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val cart = carts.get(entity)
        // Draw the cart's background.
        val background = Sprite(TextureRegion(textureCart, cartWidth * 2, 0, cartWidth, cartHeight))
        background.setPosition(position.x, position.y)
        background.setOrigin(rotationCenter.x, rotationCenter.y)
        background.rotation = cart.angle * radiansToDegrees
        background.draw(batch)
        // Draw the content.
        val fruits = Sprite(TextureRegion(textureFruits, fruitsWidth * cart.content, 0, fruitsWidth, fruitsHeight))
        fruits.setPosition(position.x + 85, position.y + 55)
        fruits.setOrigin(170f, -6f)
        fruits.rotation = cart.angle * radiansToDegrees
        fruits.draw(batch)
        // Draw the carts foreground.
        val foreground = Sprite(TextureRegion(textureCart, cartWidth, 0, cartWidth, cartHeight))
        foreground.setPosition(position.x, position.y)
        foreground.setOrigin(rotationCenter.x, rotationCenter.y)
        foreground.rotation = cart.angle * radiansToDegrees
        foreground.draw(batch)
        // Draw the wheel.
        val wheel = Sprite(textureWheel)
        val rotation = (1f - position.x / appWidth) * wheelLength * FloatMath.PI * 2f
        wheel.setOriginCenter()
        wheel.setPosition(position.x + 215, position.y + 6)
        wheel.rotation = rotation
        wheel.draw(batch)
        // Draw the bolt.
        val bolt = Sprite(TextureRegion(textureCart, 0, 0, cartWidth, cartHeight))
        bolt.setPosition(position.x, position.y)
        bolt.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { carts.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}
