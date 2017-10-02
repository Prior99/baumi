package de.cronosx.baumi.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.Vector2
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2

class Tree(
        val dna: DNA = defaultDna
) : EntitySystem() {
    var maxGeneration = 0

    val cBranch = mapperFor<Branch>()
    val cPosition = mapperFor<Position>()

    var totalTime: Float = 0f
    var root: Entity? = null

    override fun addedToEngine(engine: Engine) {
        root = engine.entity{
            with<Position>{ position = vec2(appWidth/ 2f, 20f) }
            with<Branch>{
                generation = maxGeneration
                children = ArrayList()
                length = dna.maxBranchLength
                rotation = dna.rotation
            }
        }
        onNewGeneration()
        onNewGeneration()
        onNewGeneration()
        onNewGeneration()
        onNewGeneration()
    }

    fun createBranch(pos: Vector2, rot: Float, parentBranchLength: Float): Entity {
        return engine.entity {
            with<Position>{ position = pos }
            with<Branch>{
                generation = maxGeneration + 1
                children = ArrayList()
                length = parentBranchLength * dna.perGenerationBranchLengthFactor
                rotation = rot
            }
        }
    }

    fun getChildPosition(parentPos: Position, parentBranch: Branch): Vector2 {
        val xShift = parentBranch.length * Math.cos(parentBranch.rotation.toDouble())
        val yShift = parentBranch.length * Math.sin(parentBranch.rotation.toDouble())

        return parentPos.position.cpy().add(vec2(xShift.toFloat(), yShift.toFloat()))
    }

    fun onNewGeneration() {
        val leafBranches = engine.entities
                .filter{ cBranch.has(it) }
                .filter{ cBranch.get(it).generation == maxGeneration }
        for (parent in leafBranches) {
            val parentPos = cPosition.get(parent)
            val parentBranch = cBranch.get(parent)

            val right = createBranch(
                getChildPosition(parentPos, parentBranch),
                parentBranch.rotation + Math.PI.toFloat() / 4f,
                parentBranch.length
            )
            val center = createBranch(
                getChildPosition(parentPos, parentBranch),
                parentBranch.rotation,
                parentBranch.length
            )
            val left = createBranch(
                getChildPosition(parentPos, parentBranch),
                parentBranch.rotation - Math.PI.toFloat() / 4f,
                parentBranch.length
            )

            parentBranch.children.add(left)
            parentBranch.children.add(center)
            parentBranch.children.add(right)
        }
        maxGeneration++
    }
}
