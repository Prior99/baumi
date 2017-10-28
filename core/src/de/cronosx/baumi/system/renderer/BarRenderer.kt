package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.component.Buffer
import de.cronosx.baumi.component.GroundWater
import de.cronosx.baumi.system.tick.EnergyDistribution
import de.cronosx.baumi.system.tick.Ticker
import ktx.ashley.mapperFor

class BarRenderer(val batch: Batch, engine: Engine, val ticker: Ticker) : RenderSubSystem(engine) {
    val groundWaters = mapperFor<GroundWater>()
    val buffers = mapperFor<Buffer>()

    val height = 70
    val width = 500

    val textureBarWater = Texture("bar-water.png")
    val textureBarLife = Texture("bar-life.png")

    override fun render(delta: Float) {
        // Render water.
        val groundWater = buffers.get(engine.entities.find { groundWaters.has(it) })
        val currentWater = maxOf(groundWater.current / groundWater.max, 0.05f)
        val waterBarTexture = when {
            currentWater > 0.25 -> TextureRegion(textureBarWater, 500, 0, (500 * currentWater).toInt(), 70)
            else -> TextureRegion(textureBarWater, 1000, 0, (500 * currentWater).toInt(), 70)
        }
        batch.draw(waterBarTexture, 580f, 1850f)
        batch.draw(TextureRegion(textureBarWater, 0, 0, 500, 70), 580f, 1850f)
        // Render life.
        val efficiency = (ticker.subSystems.find { it is EnergyDistribution } as EnergyDistribution).efficiency
        val normalizedEfficiency = FloatMath.clamp(efficiency / 3.0f, 0.05f, 1.0f)
        val efficienyBarTexture = when {
            normalizedEfficiency > 0.75f -> TextureRegion(textureBarLife, 1000, 0, (500 * normalizedEfficiency).toInt(), 70)
            normalizedEfficiency > 0.25f -> TextureRegion(textureBarLife, 500, 0, (500 * normalizedEfficiency).toInt(), 70)
            else -> TextureRegion(textureBarLife, 1500, 0, (500 * normalizedEfficiency).toInt(), 70)
        }
        batch.draw(efficienyBarTexture, 580f, 1780f)
        batch.draw(TextureRegion(textureBarLife, 0, 0, 500, 70), 580f, 1780f)
    }
}
