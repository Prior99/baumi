package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.Cart
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.data.config
import ktx.ashley.mapperFor

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
    val frames = 10

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val cart = carts.get(entity)
        val fruitsX = (frames.toFloat() * cart.content / config.maxCartContent).toInt() * fruitsWidth
        batch.draw(TextureRegion(textureCart, cartWidth * 2, 0, cartWidth, cartHeight), position.x, position.y)
        batch.draw(TextureRegion(textureCart, cartWidth, 0, cartWidth, cartHeight), position.x, position.y)
        batch.draw(textureWheel, position.x, position.y)
        batch.draw(TextureRegion(textureCart, 0, 0, cartWidth, cartHeight), position.x, position.y)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { carts.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}
