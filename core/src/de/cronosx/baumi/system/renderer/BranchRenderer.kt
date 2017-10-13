package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import de.cronosx.baumi.component.Branch
import de.cronosx.baumi.component.Position
import ktx.ashley.mapperFor

class BranchRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val branches = mapperFor<Branch>()
    val positions = mapperFor<Position>()
    val branchTexture = Texture("branch.png")
    val trunkTexture = Texture("trunk.png")

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val branch = branches.get(entity)
        val tex = if (branch.generation == 0) trunkTexture else branchTexture
        val sprite = Sprite(tex)
        sprite.setPosition(position.x, position.y)
        sprite.setScale(branch.length / tex.width)
        // Rotate to the given angle at the bottom center position.
        sprite.setOrigin(0f, tex.height / 2f)
        sprite.rotation = radiansToDegrees * branch.rotation
        // Adjust after rotating.
        sprite.translateY(-tex.height / 2f)
        sprite.draw(batch)
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter { branches.has(it) && positions.has(it) }
        entities.forEach { processEntity(it, delta) }
    }
}
