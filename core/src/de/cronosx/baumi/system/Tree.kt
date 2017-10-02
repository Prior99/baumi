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
    var leafProbability = 0.001f

    val cBranch = mapperFor<Branch>()
    val cPosition = mapperFor<Position>()

    var totalTime: Float = 0f
    var root: Entity? = null

    override fun addedToEngine(engine: Engine) {
        root = engine.entity{
            with<Position>{ position = vec2(appWidth/ 2f, 13f) }
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
    }

    fun createBranch(newPosition: Vector2, newRotation: Float, parentBranchLength: Float): Entity {
        val newLength = parentBranchLength * dna.perGenerationBranchLengthFactor
        val dir = getDirectionVectorAlongBranch(newLength, newRotation)
        val newChildren: MutableList<Entity> = ArrayList()
        val newGeneration = maxGeneration + 1
        var leftRight = 1.0f
        var i = 0f
        while (i < 1f) {
            if (Math.random() < leafProbability) {
                newChildren.add(engine.entity {
                    with<Position> { position = dir.cpy().scl(i).add(newPosition) }
                    with<Leaf> {
                        generation = newGeneration + 1
                        rotation = newRotation + newRotation * Math.PI.toFloat() / 8f
                    }
                })
            }
            leftRight *= -1f
            i += 0.1f
        }
        return engine.entity {
            with<Position>{ position = newPosition }
            with<Branch>{
                generation = newGeneration
                children = newChildren
                length = newLength
                rotation = newRotation
            }
        }
    }

    fun getDirectionVectorAlongBranch(length: Float, rotation: Float): Vector2 {
        return vec2(
            length * Math.cos(rotation.toDouble()).toFloat(),
            length * Math.sin(rotation.toDouble()).toFloat()
        )
    }

    fun getChildPosition(parentPos: Position, parentBranch: Branch): Vector2 {
        val dir = getDirectionVectorAlongBranch(parentBranch.length, parentBranch.rotation)
        return parentPos.position.cpy().add(dir)
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
        leafProbability *= 10f;
    }
}
