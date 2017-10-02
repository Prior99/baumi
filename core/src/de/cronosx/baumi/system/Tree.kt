package de.cronosx.baumi.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.component.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2

class Tree : EntitySystem() {
    var maxGeneration = 0

    val cBranch = mapperFor<Branch>()
    val cPosition = mapperFor<Position>()

    override fun addedToEngine(engine: Engine) {
        engine.entity{
            with<Position>{ position = vec2(appWidth/ 2f, 13f) }
            with<Branch>{
                generation = maxGeneration
                children = ArrayList()
                maxLength = 50f
                rotation = Math.PI.toFloat() / 2f
            }
        }
        onNewGeneration()
        onNewGeneration()
        onNewGeneration()
    }

    fun onNewGeneration() {
        val leafBranches = engine.entities
                .filter { cBranch.has(it) }
                .filter{ cBranch.get(it).generation == maxGeneration }
        for (parent in leafBranches) {
            val parentPos = cPosition.get(parent)
            val parentBranch = cBranch.get(parent)
            val direction = vec2(
                parentBranch.maxLength * Math.cos(parentBranch.rotation.toDouble()).toFloat(),
                parentBranch.maxLength * Math.sin(parentBranch.rotation.toDouble()).toFloat()
            )
            val shift = direction.cpy().scl(0.95f);

            val right = engine.entity {
                with<Position>{ position = parentPos.position.cpy().add(vec2(shift.x.toFloat(), shift.y.toFloat())) }
                with<Branch>{
                    generation = maxGeneration + 1
                    children = ArrayList()
                    maxLength = parentBranch.maxLength * 0.6f
                    rotation = parentBranch.rotation + Math.PI.toFloat() / 4f
                }
            }
            val center = engine.entity {
                with<Position>{ position = parentPos.position.cpy().add(vec2(shift.x.toFloat(), shift.y.toFloat())) }
                with<Branch>{
                    generation = maxGeneration + 1
                    children = ArrayList()
                    maxLength = parentBranch.maxLength * 0.6f
                    rotation = parentBranch.rotation
                }
            }
            val left = engine.entity {
                with<Position>{ position = parentPos.position.cpy().add(vec2(shift.x.toFloat(), shift.y.toFloat())) }
                with<Branch>{
                    generation = maxGeneration + 1
                    children = ArrayList()
                    maxLength = parentBranch.maxLength * 0.6f
                    rotation = parentBranch.rotation - Math.PI.toFloat() / 4f
                }
            }
            parentBranch.children.add(left)
            parentBranch.children.add(center)
            parentBranch.children.add(right)
        }
        maxGeneration++
    }
}
