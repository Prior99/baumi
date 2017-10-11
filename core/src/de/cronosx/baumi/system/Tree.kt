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

fun lerp(a: Float, b: Float, f: Float): Float {
    return a + (b - a) * f
}

class Tree() : IntervalSystem(0.01f) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    val producers = mapperFor<Producer>()

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
            with<Producer> {
                rate = 10f
            }
        }
    }

    fun createBranch(parent: Entity, rotationOffsetFixed: Float, rotationOffsetSpread: Float): Entity {
        val rotationOffset = rotationOffsetFixed + lerp(-rotationOffsetSpread, rotationOffsetSpread, Math.random().toFloat())
        val parentBranch = branches.get(parent)
        val parentGenetic = genetics.get(parent)
        val parentHealth = healths.get(parent)
        val parentConsumer = consumers.get(parent)

        val newMaxLength =
            parentGenetic.dna.length.falloff * 
            parentBranch.maxLength *
            lerp(0.9f, 1.1f, Math.random().toFloat())

        val newBranch = engine.entity {
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
                maxEnergy = parentConsumer.maxEnergy * parentGenetic.dna.energy.falloff * lerp(0.9f, 1.1f, Math.random().toFloat())
                priority = parentConsumer.priority - 100f
                rate = parentConsumer.rate * parentGenetic.dna.energy.falloff * lerp(0.9f, 1.1f, Math.random().toFloat())
            }
        }

        parentBranch.children.add(newBranch)
        return newBranch
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

        val pi = Math.PI.toFloat()

        // create left branch
        createBranch(parent, -0.2f, 0.1f * pi)

        // create right branch
        createBranch(parent, 0.2f, 0.1f * pi)

        // Sometimes, we also want to create a third branch in the middle. This is determined
        // by the `tripleProbability`.
        if (Math.random() < parentGenetic.dna.branching.tripleProbability) {
            createBranch(parent, 0f, 0.025f * pi)
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
    fun life() {
        // Of course, we only care about living entities in the life() function :)
        val livingEntities =  engine.entities
            .filter { !healths.has(it) || healths.get(it).alive }

        val producerEntities = livingEntities.filter { producers.has(it) }
        val consumerEntities = livingEntities.filter { consumers.has(it) }

        // Find out how much production and consumption we have

        val totalProduction = producerEntities
            .sumByDouble({ producers.get(it).rate.toDouble() }).toFloat()

        val totalConsumption = consumerEntities
            .sumByDouble({ consumers.get(it).rate.toDouble() }).toFloat()

        val efficiency = totalProduction / totalConsumption

        val efficiencyString = "%.1f %%".format(efficiency * 100)
        info {
            "Calculating tick $tick: Production $totalProduction, Consumption $totalConsumption, Efficiency $efficiencyString" +
            " for ${engine.entities.count()} entities:"
        }

        // Give everybody
        var currentContingent  = 0f
        for (entity in consumerEntities) {
            val consumer = consumers.get(entity)
            // Add the requested amount according to global efficiency, but
            // always subtract the full rate too.
            consumer.energy += consumer.rate * (efficiency - 1)

            if (consumer.energy > consumer.maxEnergy) {
                currentContingent += consumer.energy - consumer.maxEnergy
                consumer.energy = consumer.maxEnergy
            } else if(consumer.energy < consumer.minEnergy && healths.has(entity)) {
                // If the energy sank below the minimum and the entity has
                // health, impact it.  The rate of health loss is proportional
                // to the amount of energy missing below the minimum energy 
                val lossRate = 1 - consumer.energy / consumer.minEnergy 
                val loss = lossRate * 0.1f
                info { "    Reducing health of entity by $loss." }
                val health = healths.get(entity)
                health.current -= loss
            }
        }

        // 3. Handle branches
        for (entity in consumerEntities) {
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
        for (entity in consumerEntities) {
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
            life()
            adjust()
        }
        info { "    Tick took ${time}ms." }
    }
}
