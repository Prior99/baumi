package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
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
import ktx.log.info

class BranchRenderer(var batch: Batch) : SortedIteratingSystem(
        allOf(Branch::class, Position::class).get(), ZComparator()) {
    val cBranch = mapperFor<Branch>()
    val cPosition = mapperFor<Position>()
    val branchTexture = Texture("dark-wood.png")

    override fun processEntity(entity: Entity, delta: Float) {
        val position = cPosition.get(entity).position
        val branch = cBranch.get(entity)
        val sprite = Sprite(branchTexture)
        sprite.setScale(branch.maxLength / branchTexture.width)
        sprite.setOrigin(0f, branchTexture.height / 2f)
        sprite.rotation = radiansToDegrees * branch.rotation
        sprite.setPosition(position.x, position.y)
        sprite.draw(batch)
    }

    class ZComparator : Comparator<Entity> {
        val cBranch = mapperFor<Branch>()

        override fun compare(e1: Entity, e2: Entity): Int {
            return cBranch.get(e1).generation - cBranch.get(e2).generation;
        }
    }
}
