package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils.radiansToDegrees
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import de.cronosx.baumi.component.Branch
import de.cronosx.baumi.component.Position
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class BranchRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val branches = mapperFor<Branch>()
    val positions = mapperFor<Position>()
    val branchTexture = Texture("branch.png")
    val trunkTexture = Texture("trunk.png")
    val energyTexture = Texture("energy.png")
    var time = 0f

    fun processEntity(entity: Entity, delta: Float) {
        val position = positions.get(entity).position
        val branch = branches.get(entity)
        val tex = if (branch.generation == 0) trunkTexture else branchTexture
        val sprite = Sprite(tex)
        sprite.setScale(branch.length / branchTexture.width)
        sprite.setOrigin(0f, branchTexture.height / 2f)
        sprite.rotation = radiansToDegrees * branch.rotation
        sprite.setPosition(position.x, position.y)
        sprite.draw(batch)
        val index = Math.floor((time / branch.length).toDouble() * 6.0).toInt() % 15
        val region = TextureRegion(energyTexture, 0, 100 * index, 500, 100)
        val energySprite = Sprite(region)
        energySprite.setScale(branch.length / branchTexture.width)
        energySprite.setOrigin(0f, branchTexture.height / 2f)
        energySprite.rotation = radiansToDegrees * branch.rotation
        energySprite.setPosition(position.x, position.y)
        energySprite.draw(batch)
        time += delta
    }

    override fun render(delta: Float) {
        val entities = engine.entities.filter{ branches.has(it) && positions.has(it) }
        entities.forEach{ processEntity(it, delta) }
    }
}
