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

class Tree() : IntervalSystem(1f) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()

    var root: Entity? = null

    override fun addedToEngine(engine: Engine) {
        root = engine.entity{
            with<Position>{
                position = vec2(appWidth/ 2f, 13f)
            }
            with<Branch> {
                rotation = dna.rotation.initial
                length = dna.length.initial
                maxLength = dna.length.max
                leafProbability = dna.leafs.leafGrowProbability
                children = ArrayList()
            }
            with<Genetic> {
                dna = defaultDna
            }
            with<Health> {
                max = dna.health.max
                current = dna.health.max
            }
            with<Consumer> {
                maxEnergy = dna.energy.max,
                rate = dna.energy.upkeep
            }
        }
    }

    fun createBranch(newPosition: Vector2, parent: Entity, rotationOffset: Float): Entity {
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)
        val parentHealth = healths.get(parent)

        val newLength = 0f
        val newRotation = parentBranch.rotation + rotationOffset
        val newChildren: MutableList<Entity> = ArrayList()
        val newGeneration = parentBranch.generation + 1
        val newLeafProbability = parentBranch.leafProbability * 10f
        val newMaxHealth = parentHealth.max* parentGenetic.dna.health.falloff
        val newMaxLength =
            parentGenetic.dna.perGenerationBranchLengthFactor * parentBranch.maxLength +
            Math.random().toFloat() * 0.2f - 0.1f
        return engine.entity {
            with<Position> {
                position = newPosition
            }
            with<Branch> {
                rotation = newRotation
                length = newLength
                maxLength = newMaxLength
                generation = newGeneration
                leafProbability = newLeafProbability
                children = newChildren
            }
            with<Genetic> {
                dna = parentGenetic.dna
            }
            with<Health> {
                max = newMaxHealth
                current = newMaxHealth
            }
            with<Consumer> {
                maxStorage = parent.maxStorage * dna.maxStorageFalloff
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
        val parentPosition = positions.get(parent)
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)

        val newPosition = getChildPosition(parentPosition, parentBranch)
        val rightAngle = 0.1f + Math.random().toFloat() * 0.2f
        val leftAngle = 0.1f + Math.random().toFloat() * 0.2f
        val right = createBranch(newPosition, parentBranch, Math.PI.toFloat() * rightAngle)
        val left = createBranch(newPosition, parentBranch, -Math.PI.toFloat() * leftAngle)
        parentBranch.children.add(left)
        parentBranch.children.add(right)
        if (Math.random() < parentGenetic.dna.tripleProbability) {
            val centerAngle = Math.random().toFloat() * 0.05f
            val center = createBranch(newPosition, parent, Math.PI.toFloat() * centerAngle)
            parentBranch.children.add(center)
        }
    }

    fun growNewBranches(entity: Entity) {
        val branch = branches.get(entity)
        val branchingGene = genetics.get(entity).dna.branching
        val consumer = consumers.get(entity)

        val childBranches = branch.children.filter{ branches.has(it) }
        val childBranchCount = childBranches.count()
        // The branch can only create new branches branches if ...
        // ... the branch didn't do so already
        val canGrow = childBranchCount == 0 &&
            // ... the branch is old enough
            branch.length > branchingGene.minLength * branch.maxLength &&
            // ... the branch isn't above the maximum generation
            branch.generation < branchingGene.maxDepth &&
            // ... enough energy is available
            consumer.energy > branchingGene.branchCost
        if (!canGrow) {
            return
        }
        consumer.storage -= branchingGene.branchingCost
        createNextGeneration(entity)
    }

    fun growLength(entity: Entity) {
        val branch = branches.get(entity)
        val lengthGene = genetics.get(entity).dna.length
        val consumer = consumers.get(entity)

        // Don't grow longer than the maximum.
        if (branch.length > branch.maxLength) {
            return
        }
        if (consumer.energy > lengthGene.growCost) {
            branch.length += lengthGene.growthSpeed
        }
    }

    fun growLeafs(entity: Entity, delta: Float) {
        val branch = branches.get(entity)
        val leafsGene = genetics.get(entity).dna.leafs
        val position = positions.get(entity).position

        val leafCount = branch.children.filter{ leafs.has(it) }.count()
        if (leafCount > leafsGene.max) {
            return
        }

        val randomPositionAlongBranch = Math.random().toFloat()
        val rotationOffset = Math.random().toFloat() * leafsGene.maxRotationOffset * 2f - leafGene.maxRotationOffset
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

    /**
     * This function distributes the specified amount of energy across all entities.
     * @param energy The amount of energy to distribute across the system.
     */
    fun life(energy: Float) {
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

    /**
     * After all branches changed size and rotations the branches need to be readjusted.
     * This function recursively loops over all branches and fixes their positions after
     * each tick's update.
     */
    fun adjust(entity: Entity, newPos?: Vector2) {
        val position = positions.get(entity)
        if (newPos != null) {
            position.position = newPos
        }
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

    override fun updateInterval() {
        val rootPosition = positions.get(root)
        val currentRoot = root
        if (currentRoot != null) {
            life(2f);
            adjust(currentRoot)
        }
    }
}
