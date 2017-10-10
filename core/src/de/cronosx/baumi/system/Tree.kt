package de.cronosx.baumi.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
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
                rotation = defaultDna.rotation.initial
                length = defaultDna.length.initial
                maxLength = defaultDna.length.max
                leafProbability = defaultDna.leafs.leafGrowProbability
                children = ArrayList()
            }
            with<Genetic> {
                dna = defaultDna
            }
            with<Health> {
                max = defaultDna.health.max
                current = defaultDna.health.max
            }
            with<Consumer> {
                maxEnergy = defaultDna.energy.max
                rate = defaultDna.energy.upkeep
                priority = 1000f
            }
        }
    }

    fun createBranch(newPosition: Vector2, parent: Entity, rotationOffset: Float): Entity {
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)
        val parentHealth = healths.get(parent)
        val parentConsumer = consumers.get(parent)

        val newLength = 0f
        val newRotation = parentBranch.rotation + rotationOffset
        val newChildren: MutableList<Entity> = ArrayList()
        val newGeneration = parentBranch.generation + 1
        val newLeafProbability = parentBranch.leafProbability * 10f
        val newMaxHealth = parentHealth.max* parentGenetic.dna.health.falloff
        val newMaxLength =
            parentGenetic.dna.length.falloff * parentBranch.maxLength +
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
                maxEnergy = parentConsumer.maxEnergy * parentGenetic.dna.energy.falloff
                priority = parentConsumer.priority - 100f
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
        val right = createBranch(newPosition, parent, Math.PI.toFloat() * rightAngle)
        val left = createBranch(newPosition, parent, -Math.PI.toFloat() * leftAngle)
        parentBranch.children.add(left)
        parentBranch.children.add(right)
        if (Math.random() < parentGenetic.dna.branching.tripleProbability) {
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
        consumer.energy -= branchingGene.branchCost
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

    fun growLeafs(entity: Entity) {
        val branch = branches.get(entity)
        val leafsGene = genetics.get(entity).dna.leafs
        val parentBranch = positions.get(entity).position

        val leafCount = branch.children.filter{ leafs.has(it) }.count()
        if (leafCount > leafsGene.max) {
            return
        }

        val randomPositionAlongBranch = Math.random().toFloat()
        val rotationOffset = Math.random().toFloat() * leafsGene.maxRotationOffset * 2f - leafsGene.maxRotationOffset
        val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
        branch.children.add(engine.entity {
            with<Position> {
                position = dir.cpy().scl(randomPositionAlongBranch).add(parentBranch)
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
     * @param contingent The amount of energy available to the system.
     */
    fun life(initialContingent: Float) {
        val sortedEntities = engine.entities
            .filter{ consumers.has(it) }
            .sortedWith(compareBy{ consumers.get(it).priority })
        var currentContingent = initialContingent
        for (entity in sortedEntities) {
            val consumer = consumers.get(entity)
            // If the contingent is large enough to fullfill the consumer's needs, reduce the contingent and continue.
            if (currentContingent + consumer.energy > consumer.rate) {
                val contingentPart = minOf(consumer.rate, currentContingent)
                currentContingent -= contingentPart
                consumer.energy -= consumer.rate - contingentPart
                continue
            }
            // If not and the consumer has a health component, impact it.
            if (healths.has(entity)) {
                val loss = consumer.rate - maxOf(currentContingent, 0f)
                info { "Reducing health of entity by ${loss}." }
                healths.get(entity).current -= loss
            }
        }
    }

    /**
     * After all branches changed size and rotations the branches need to be readjusted.
     * This function recursively loops over all branches and fixes their positions after
     * each tick's update.
     */
    fun adjust(entity: Entity? = root, newPos: Vector2? = null) {
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
        life(2f)
        adjust()
    }
}
