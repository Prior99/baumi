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
import ktx.log.*

class Tree(
        val dna: DNA = defaultDna,
        val initialSize: Float = 10f
) : EntitySystem() {

    val cBranch = mapperFor<Branch>()
    val cLeaf = mapperFor<Leaf>()
    val cPosition = mapperFor<Position>()

    var totalTime: Float = 0f
    var root: Entity? = null

    override fun addedToEngine(engine: Engine) {
        root = engine.entity{
            with<Position>{ position = vec2(appWidth/ 2f, 13f) }
            with<Branch>{
                children = ArrayList()
                length = initialSize
                rotation = dna.rotation
                leafProbability = 0.0001f
                maxLength = dna.maxLength
            }
        }
    }

    fun createBranch(newPosition: Vector2, parent: Branch, rotationOffset: Float): Entity {
        val newLength = 0f
        val newRotation = parent.rotation + rotationOffset
        val newChildren: MutableList<Entity> = ArrayList()
        val newGeneration = parent.generation + 1
        val newLeafProbability = parent.leafProbability * 10f
        return engine.entity {
            with<Position>{ position = newPosition }
            with<Branch>{
                generation = newGeneration
                children = newChildren
                length = newLength
                rotation = newRotation
                leafProbability = newLeafProbability
                maxLength = dna.perGenerationBranchLengthFactor * parent.maxLength +
                    Math.random().toFloat() * 0.2f - 0.1f
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

    fun newGeneration(parent: Entity) {
        val parentPos = cPosition.get(parent)
        val parentBranch = cBranch.get(parent)
        val newPosition = getChildPosition(parentPos, parentBranch)
        if (Math.random() < dna.tripleProbability) {
            val rightAngle = 0.2f + Math.random().toFloat() * 0.1f
            val leftAngle = 0.2f + Math.random().toFloat() * 0.1f
            val centerAngle = Math.random().toFloat() * 0.05f
            val right = createBranch(newPosition, parentBranch, Math.PI.toFloat() * rightAngle)
            val center = createBranch(newPosition, parentBranch, Math.PI.toFloat() * centerAngle)
            val left = createBranch(newPosition, parentBranch, -Math.PI.toFloat() * leftAngle)
            parentBranch.children.add(left)
            parentBranch.children.add(center)
            parentBranch.children.add(right)
            return
        }
        val rightAngle = 0.1f + Math.random().toFloat() * 0.2f
        val leftAngle = 0.1f + Math.random().toFloat() * 0.2f
        val right = createBranch(newPosition, parentBranch, Math.PI.toFloat() * rightAngle)
        val left = createBranch(newPosition, parentBranch, -Math.PI.toFloat() * leftAngle)
        parentBranch.children.add(left)
        parentBranch.children.add(right)
    }

    fun grow(entity: Entity, delta: Float): Boolean {
        if (!cBranch.has(entity) || !cPosition.has(entity)) {
            return false
        }

        val branch = cBranch.get(entity)
        val branchPosition = cPosition.get(entity).position

        val childBranches = branch.children.filter{ cBranch.has(it) }
        val childBranchCount = childBranches.count()
        val generateBranches = childBranchCount == 0 &&
            branch.length > dna.minLengthGenerationThreshold * branch.maxLength &&
            Math.random() < dna.generateProbability
        if (generateBranches) {
            newGeneration(entity)
            return true
        }

        val handToChildren = Math.random() < dna.handToChildrenProbability
        if (childBranchCount == 0 || !handToChildren) {
            if (branch.length < branch.maxLength) {
                branch.length += delta * dna.growthSpeed * branch.maxLength
                adjust(entity, branchPosition)
                return true
            }

        }
        val growLeafs = Math.random() < dna.leafGrowProbability
        if ((!growLeafs || handToChildren) && childBranchCount > 0) {
            val index = Math.floor(Math.random() * childBranchCount).toInt()
            val child = childBranches[index]
            val childCouldGrow = grow(child, delta)
            if (childCouldGrow) {
                return true
            }
        }
        val leafCount = branch.children.filter{ cLeaf.has(it) }.count()
        if (Math.random() < branch.leafProbability && leafCount < dna.maxLeafCount) {
            val i = Math.random().toFloat()
            val offset = Math.random().toFloat() * 0.2f - 0.1f
            val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
            branch.children.add(engine.entity {
                with<Position> { position = dir.cpy().scl(i).add(branchPosition) }
                with<Leaf> {
                    generation = branch.generation + 1
                    rotation = branch.rotation + offset * Math.PI.toFloat() / 8f
                }
            })
            return true
        }
        return false
    }

    fun adjust(entity: Entity, newPos: Vector2) {
        val branchPosition = cPosition.get(entity)
        branchPosition.position = newPos
        if (!cBranch.has(entity)) {
            return
        }
        val branch = cBranch.get(entity)
        val childPosition = getChildPosition(branchPosition, branch)
        for (child in branch.children) {
            adjust(child, childPosition)
        }
    }

    override fun update(delta: Float) {
        totalTime += delta

        val currentRoot = root
        if (currentRoot == null) {
            return
        }

        val rootPosition = cPosition.get(currentRoot)

        grow(currentRoot, delta);
    }
}
