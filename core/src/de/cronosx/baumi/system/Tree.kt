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
import kotlin.system.measureTimeMillis

class Tree() : IntervalSystem(0.01f) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()

    var root: Entity? = null
    var tick = 0

    override fun addedToEngine(engine: Engine) {
        root = engine.entity {
            with<Position> {
                position = vec2(appWidth/ 2f, 320f)
            }
            with<Branch> {
                rotation = defaultDna.rotation.initial
                length = defaultDna.length.initial
                maxLength = defaultDna.length.max
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

    fun createBranch(parent: Entity, rotationOffset: Float): Entity {
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)
        val parentHealth = healths.get(parent)
        val parentConsumer = consumers.get(parent)

        val newMaxLength =
            parentGenetic.dna.length.falloff * parentBranch.maxLength +
            Math.random().toFloat() * 0.2f - 0.1f
        return engine.entity {
            with<Position> {} // Will be adjusted anyway.
            with<Branch> {
                rotation = parentBranch.rotation + rotationOffset
                maxLength = newMaxLength
                generation = parentBranch.generation + 1
                children = ArrayList()
            }
            with<Genetic> {
                dna = parentGenetic.dna
            }
            with<Health> {
                max = parentHealth.max * parentGenetic.dna.health.falloff
                current = max
            }
            with<Consumer> {
                maxEnergy = parentConsumer.maxEnergy * parentGenetic.dna.energy.falloff
                priority = parentConsumer.priority - 100f
                rate = parentConsumer.rate * parentGenetic.dna.energy.falloff
            }
        }
    }

    fun getDirectionVectorAlongBranch(length: Float, rotation: Float): Vector2 {
        return vec2(
            length * Math.cos(rotation.toDouble()).toFloat(),
            length * Math.sin(rotation.toDouble()).toFloat()
        )
    }

    fun getChildPosition(parentPos: Position, parentBranch: Branch, positionAlongBranch: Float = 0.94f): Vector2 {
        val dir = getDirectionVectorAlongBranch(parentBranch.length, parentBranch.rotation)
        return parentPos.position.cpy().add(dir.scl(positionAlongBranch))
    }

    fun growBranches(parent: Entity) {
        val parentPosition = positions.get(parent)
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)

        val rightAngle = 0.1f + Math.random().toFloat() * 0.2f
        val leftAngle = 0.1f + Math.random().toFloat() * 0.2f
        val right = createBranch(parent, Math.PI.toFloat() * rightAngle)
        val left = createBranch(parent, -Math.PI.toFloat() * leftAngle)
        parentBranch.children.add(left)
        parentBranch.children.add(right)
        if (Math.random() < parentGenetic.dna.branching.tripleProbability) {
            val centerAngle = Math.random().toFloat() * 0.05f
            val center = createBranch(parent, Math.PI.toFloat() * centerAngle)
            parentBranch.children.add(center)
        }
    }

    fun growLength(entity: Entity) {
        val branch = branches.get(entity)
        val lengthGene = genetics.get(entity).dna.length
        branch.length += lengthGene.growthSpeed
    }

    fun growLeaf(entity: Entity) {
        val branch = branches.get(entity)
        val leafsGene = genetics.get(entity).dna.leafs
        val parentConsumer = consumers.get(entity)
        val parentPosition = positions.get(entity).position

        val randomPositionAlongBranch = Math.random().toFloat()
        val rotationOffset = Math.random().toFloat() * leafsGene.maxRotationOffset * 2f - leafsGene.maxRotationOffset
        val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
        branch.children.add(engine.entity {
            with<Position> {
                position = dir.cpy().scl(randomPositionAlongBranch) + parentPosition
            }
            with<Leaf> {
                generation = branch.generation + 1
                rotation = branch.rotation + rotationOffset * Math.PI.toFloat()
                positionAlongBranch = randomPositionAlongBranch
            }
            with<Consumer> {
                maxEnergy = leafsGene.maxEnergy
                priority = parentConsumer.priority - 1000f
                rate = leafsGene.upkeep
            }
            with<Health> {
                current = leafsGene.maxHealth
                max = leafsGene.maxHealth
            }
        })
    }


    fun getMaxGeneration(entity: Entity): Int {
        if (!branches.has(entity)) {
            return 0
        }
        val branch = branches.get(entity)
        return maxOf(branch.children.map { getMaxGeneration(it) }.max() ?: 0, branch.generation)
    }

    fun maxLeafCount(entity: Entity): Int {
        val branch = branches.get(entity)
        val dna = genetics.get(entity).dna
        val relativeDepth = getMaxGeneration(entity) - branch.generation
        return Math.floor(Math.pow(dna.leafs.leafCountFalloff.toDouble(), relativeDepth.toDouble()) *
            dna.leafs.maxGenerationLeafCountPerLength * branch.length).toInt()
    }

    /**
     * This function distributes the specified amount of energy across all entities.
     * @param contingent The amount of energy available to the system.
     * @return The amount of energy consumed.
     */
    fun life(initialContingent: Float): Float {
        info {
            "Calculating tick $tick with contingent of $initialContingent" +
            " for ${engine.entities.count()} entities:"
        }
        val sortedEntities = engine.entities
            .filter { consumers.has(it) }
            .filter { !healths.has(it) || healths.get(it).alive }
            .sortedWith(compareBy { -consumers.get(it).priority })
        var currentContingent = initialContingent
        // 1. Make sure nobody dies.
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
                info { "    Reducing health of entity by $loss." }
                val health = healths.get(entity)
                health.current -= loss
            }
        }
        if (currentContingent <= 0) {
            info { "    => Contingent depleted after upkeep." }
            return initialContingent - currentContingent
        }
        // 2. Fill buffers.
        for (entity in sortedEntities) {
            val consumer = consumers.get(entity)
            val energyGain = minOf(currentContingent, consumer.remainingBufferCapacity)
            if (energyGain > 0) {
                info { "    Increasing buffer from ${consumer.energy} to ${consumer.energy + energyGain}." }
            }
            currentContingent -= energyGain
            consumer.energy += energyGain
        }
        if (currentContingent <= 0) {
            info { "    => Contingent depleted after filling energy storages." }
            return initialContingent - currentContingent
        }
        // 3. Handle branches
        for (entity in sortedEntities) {
            if (!branches.has(entity) || !genetics.has(entity)) {
                continue
            }
            val branch = branches.get(entity)
            val dna = genetics.get(entity).dna
            // 3.1. Growing leafs
            val canGrowLeaf =
                branch.children.filter { leafs.has(it) }.count() < maxLeafCount(entity) &&
                currentContingent >= dna.leafs.leafCost
            if (canGrowLeaf) {
                info { "Growing a leaf." }
                growLeaf(entity)
                currentContingent -= dna.leafs.leafCost
            }
            // 3.2. Branching
            val canGrowBranches = branch.children.filter { branches.has(it) }.count() == 0 &&
                branch.length > dna.branching.minLength * branch.maxLength &&
                branch.generation < dna.branching.maxDepth &&
                currentContingent >= dna.branching.branchCost
            if (canGrowBranches) {
                info { "Growing branches." }
                growBranches(entity)
                currentContingent -= dna.branching.branchCost
            }
            // 3.3. Growing length
            val canGrowLength = branch.length < branch.maxLength &&
                currentContingent >= dna.length.growCost
            if (canGrowLength) {
                info { "Growing length." }
                growLength(entity)
                currentContingent -= dna.length.growCost
            }
        }
        // 4. Kill obsolete leafs
        for (entity in sortedEntities) {
            if (!branches.has(entity)) {
                continue
            }
            val branch = branches.get(entity)
            val livingLeafs = branch.children.filter { leafs.has(it) && healths.has(it) && healths.get(it).alive }
            val maxLeafs = maxLeafCount(entity)
            if (livingLeafs.count() <= maxLeafs) {
                continue
            }
            for (i in 0..maxLeafs) {
                healths.get(livingLeafs[i]).kill()
            }
        }
        if (currentContingent > 0) {
            info { "    => Contingent was not depleted. Contingent of $currentContingent left." }
        }
        return initialContingent - currentContingent
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
        val time = measureTimeMillis {
            tick++
            life(10f)
            adjust()
        }
        info { "    Tick took ${time}ms." }
    }
}
