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

class Tree() : EntitySystem() {
    val branches = mapperFor<Branch>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    var root: Entity? = null

    override fun addedToEngine(engine: Engine) {
        root = engine.entity{
            with<Position>{ position = vec2(appWidth/ 2f, 13f) }
            with<Branch>{
                children = ArrayList()
                length = defaultDna.initialSize
                rotation = defaultDna.rotation
                leafProbability = 0.0001f
                maxLength = defaultDna.maxLength
                dna = defaultDna
                maxStorage = dna.maxStorage
                maxHealth = dna.maxHealth
                health = dna.maxHealth
            }
        }
    }

    fun createBranch(newPosition: Vector2, parent: Branch, rotationOffset: Float): Entity {
        val newLength = 0f
        val newRotation = parent.rotation + rotationOffset
        val newChildren: MutableList<Entity> = ArrayList()
        val newGeneration = parent.generation + 1
        val newLeafProbability = parent.leafProbability * 10f
        val newMaxHealth = parent.maxHealth * parent.dna.maxHealthFalloff
        return engine.entity {
            with<Position>{ position = newPosition }
            with<Branch>{
                generation = newGeneration
                children = newChildren
                length = newLength
                rotation = newRotation
                leafProbability = newLeafProbability
                dna = parent.dna
                maxLength = parent.dna.perGenerationBranchLengthFactor * parent.maxLength +
                    Math.random().toFloat() * 0.2f - 0.1f
                maxStorage = parent.maxStorage * dna.maxStorageFalloff
                maxHealth = newMaxHealth
                health = newMaxHealth
            }
        }
    }

    fun getDirectionVectorAlongBranch(length: Float, rotation: Float): Vector2 {
        return vec2(
            length * Math.cos(rotation.toDouble()).toFloat(),
            length * Math.sin(rotation.toDouble()).toFloat()
        )
    }

    fun getChildPosition(parentPos: Position, parentBranch: Branch, positionAlongBranch: Float = 1f): Vector2 {
        val dir = getDirectionVectorAlongBranch(parentBranch.length, parentBranch.rotation)
        return parentPos.position.cpy().add(dir.scl(positionAlongBranch))
    }

    fun createNextGeneration(parent: Entity) {
        val parentPos = positions.get(parent)
        val parentBranch = branches.get(parent)
        val newPosition = getChildPosition(parentPos, parentBranch)
        val rightAngle = 0.1f + Math.random().toFloat() * 0.2f
        val leftAngle = 0.1f + Math.random().toFloat() * 0.2f
        val right = createBranch(newPosition, parentBranch, Math.PI.toFloat() * rightAngle)
        val left = createBranch(newPosition, parentBranch, -Math.PI.toFloat() * leftAngle)
        parentBranch.children.add(left)
        parentBranch.children.add(right)
        if (Math.random() < parentBranch.dna.tripleProbability) {
            val centerAngle = Math.random().toFloat() * 0.05f
            val center = createBranch(newPosition, parentBranch, Math.PI.toFloat() * centerAngle)
            parentBranch.children.add(center)
        }
    }

    fun growNewBranches(entity: Entity, delta: Float) {
        val branch = branches.get(entity)
        val childBranches = branch.children.filter{ branches.has(it) }
        val childBranchCount = childBranches.count()
        // The branch can only create new branches branches if ...
        // ... the branch didn't do so already
        val canGrow = childBranchCount == 0 &&
            // ... the branch is old enough
            branch.length > branch.dna.minLengthGenerationThreshold * branch.maxLength &&
            // ... the branch isn't above the maximum generation
            branch.generation < branch.dna.maxGeneration &&
            // ... enough energy is available
            branch.getSurplus() > branch.dna.branchingCost
        if (!canGrow) {
            return
        }
        branch.storage -= branch.dna.branchingCost
        createNextGeneration(entity)
        upKeepChildren(entity, delta)
    }

    fun growLength(entity: Entity, delta: Float) {
        val branch = branches.get(entity)
        if (branch.length > branch.maxLength) {
            return
        }
        val theoreticalGrowAmount = delta * branch.dna.growthSpeed
        val theoreticalGrowCost = theoreticalGrowAmount * branch.dna.growCost
        val growBudget = branch.getSurplus() * branch.dna.surplusGrowUsageFactor
        val actualGrowAmount = if (theoreticalGrowCost <= growBudget) theoreticalGrowAmount else
            growBudget / branch.dna.growCost
        branch.length += actualGrowAmount
        val branchPosition = positions.get(entity).position
        adjust(entity, branchPosition)
    }

    fun upKeepChildren(entity: Entity, delta: Float) {
        val branch = branches.get(entity)
        val branchCount = branch.childBranches().count() + 1 // Including parent.
        val perBranch = branch.getSurplus() + branchCount
        for (child in branch.children) {
            if (branches.has(child)) {
                val childBranch = branches.get(child)
                life(child, delta, perBranch)
                branch.storage -= perBranch
            }
        }
    }

    fun growLeafs(entity: Entity, delta: Float) {
        val branch = branches.get(entity)
        val leafCount = branch.children.filter{ leafs.has(it) }.count()
        if (leafCount > branch.getMaxLeafs()) {
            return
        }
        val branchPosition = positions.get(entity).position
        val randomPositionAlongBranch = Math.random().toFloat()
        val rotationOffset =
            Math.random().toFloat() * branch.dna.maxLeafRotationOffset * 2f - branch.dna.maxLeafRotationOffset
        val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
        branch.children.add(engine.entity {
            with<Position> {
                position = dir.cpy().scl(randomPositionAlongBranch).add(branchPosition)
            }
            with<Leaf> {
                generation = branch.generation + 1
                rotation = branch.rotation + rotationOffset * Math.PI.toFloat()
                positionAlongBranch = randomPositionAlongBranch
            }
        })
    }

    fun life(entity: Entity, delta: Float, energy: Float) {
        // Ignore malformed entities
        if (!branches.has(entity) || !positions.has(entity)) {
            return
        }
        val branch = branches.get(entity);
        // Add the new energy to the branche's storage.
        branch.storage += energy;
        info { "Adding ${energy} energy to branch in gen ${branch.generation}." }
        info { "     Storage before upkeep: ${branch.storage}." }
        // Upkeeping.
        branch.storage -= branch.dna.upKeep * delta
        info { "     Storage after upkeep: ${branch.storage}." }
        // If the branch ran out of energy while upkeeping itself, ...
        if (branch.storage < 0) {
            // ... impact the health.
            branch.health += branch.storage
            branch.storage = 0f
            info { "Reducing health of branch in gen ${branch.generation} by ${branch.storage}" }
        }
        // Don't iterate dead branches.
        if (branch.dead()) {
            return
        }
        // Make sure nobody dies.
        upKeepChildren(entity, delta)
        while (branch.storage > branch.maxStorage) {
            growNewBranches(entity, delta)
            growLength(entity, delta)
            growLeafs(entity, delta) // Not yet ported to energy system.
        }
        branch.storage = Math.min(branch.storage, branch.maxStorage)
    }

    fun adjust(entity: Entity, newPos: Vector2) {
        val position = positions.get(entity)
        position.position = newPos
        if (!branches.has(entity)) {
            return
        }
        val branch = branches.get(entity)
        for (child in branch.children) {
            if (leafs.has(child)) {
                val leaf = leafs.get(child)
                var childPosition = positions.get(child)
                childPosition.position = getChildPosition(position, branch, leaf.positionAlongBranch)
            } else {
                adjust(child, getChildPosition(position, branch))
            }
        }
    }

    override fun update(delta: Float) {
        val rootPosition = positions.get(root)
        val currentRoot = root
        if (currentRoot != null) {
            life(currentRoot, delta, delta * 10f);
        }
    }
}
