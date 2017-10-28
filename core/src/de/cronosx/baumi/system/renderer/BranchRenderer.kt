package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.component.*
import ktx.ashley.mapperFor
import ktx.log.info

class BranchRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val branches = mapperFor<Branch>()
    val children = mapperFor<Child>()
    val consumers = mapperFor<Consumer>()
    val positions = mapperFor<Position>()
    val healths = mapperFor<Health>()
    val branchTexture = Texture("branch.png")
    val energyTexture = Texture("energy.png")
    val trunkTexture = Texture("trunk.png")

    val frames = 12
    val virtualFrames = 200
    var time = 0f

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val branch = branches.get(entity)
        val child = children.get(entity)
        val health = healths.get(entity)
        val tex = if (child.generation == 0) trunkTexture else branchTexture
        val sprite = Sprite(tex)
        sprite.setPosition(position.x, position.y)
        sprite.setScale(branch.length / tex.width)
        // Rotate to the given angle at the bottom center position.
        sprite.setOrigin(0f, tex.height / 2f)
        sprite.rotation = radiansToDegrees * branch.rotation
        // Adjust after rotating.
        sprite.translateY(-tex.height / 2f)
        sprite.draw(batch)
        val consumer = consumers.get(entity)
        // Draw energy.
        if (!health.alive) {
            return
        }
        val strength = maxOf((consumer.energy - consumer.minEnergy) / (consumer.maxEnergy - consumer.minEnergy), 0f)
        for (i in 0 .. (strength * frames).toInt()) {
            val frame = (i + (child.generation % 2) * virtualFrames + time.toInt()) % virtualFrames
            if (frame >= 12) {
                continue
            }
            val energy = Sprite(TextureRegion(energyTexture, 0, (frame.toInt() % frames) * 100, energyTexture.width, 100))
            energy.setAlpha(strength)
            energy.setPosition(position.x, position.y)
            energy.setScale(branch.length / energyTexture.width.toFloat())
            energy.setOrigin(0f, 100f / 2f)
            energy.rotation = radiansToDegrees * branch.rotation
            energy.translateY(-tex.height / 2f)
            energy.draw(batch)
        }
        time += delta / 2f
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { branches.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}
