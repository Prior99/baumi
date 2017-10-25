package de.cronosx.baumi.system

import de.cronosx.baumi.system.renderer.*

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.data.debug
import de.cronosx.baumi.system.tick.Ticker

class Renderer(var batch: Batch, val ticker: Ticker) : EntitySystem() {
    val textureBackground = Texture("background.png")
    val textureGrass = Texture("grass.png")

    var branches: BranchRenderer? = null
    var rain: RainDropRenderer? = null
    var groundWater: GroundWaterRenderer? = null
    var leafs: LeafRenderer? = null
    var fruits: FruitRenderer? = null
    var clouds: CloudRenderer? = null
    var cart: CartRenderer? = null
    var loading: LoadingRenderer? = null
    var fertilizerBags: FertilizerBagRenderer? = null
    var fertilizerGrains: FertilizerGrainRenderer? = null

    override fun addedToEngine(engine: Engine) {
        branches = BranchRenderer(batch, engine)
        rain = RainDropRenderer(batch, engine)
        groundWater = GroundWaterRenderer(batch, engine)
        leafs = LeafRenderer(batch, engine)
        fruits = FruitRenderer(batch, engine)
        clouds = CloudRenderer(batch, engine)
        cart = CartRenderer(batch, engine)
        loading = LoadingRenderer(batch, engine, ticker)
        fertilizerBags = FertilizerBagRenderer(batch, engine)
        fertilizerGrains = FertilizerGrainRenderer(batch, engine)
        batch.enableBlending()
    }

    override fun update(delta: Float) {
        if (debug.disableRendering) return
        batch.begin()
        batch.draw(textureBackground, 0f, 0f)
        branches?.render(delta)
        groundWater?.render(delta)
        branches?.render(delta)
        branches?.render(delta)
        batch.draw(textureGrass, 0f, 0f)
        leafs?.render(delta)
        fruits?.render(delta)
        cart?.render(delta)
        rain?.render(delta)
        clouds?.render(delta)
        fertilizerBags?.render(delta)
        fertilizerGrains?.render(delta)
        loading?.render(delta)
        batch.end()
    }
}
