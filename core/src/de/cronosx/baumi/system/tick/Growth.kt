package de.cronosx.baumi.system.tick

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

/**
 * `lerp` is a linear interpolation method, adjusting `a` to move towards `b` with the
 * speed of `f`.
 */
fun lerp(a: Float, b: Float, f: Float): Float {
    return a + (b - a) * f
}

class Growth(engine: Engine) : TickSubSystem(engine) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    val roots = mapperFor<Root>()

    init {
        // Create the root tree branch.
        engine.entity {
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
                minEnergy = maxEnergy / 2f
                rate = defaultDna.energy.upkeep
            }
            with<Producer> {
                rate = 10f
            }
            with<Root> {}
        }
    }

    /**
     * Create a new branch forked from the `parent` entity with the given rotations.
     */
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
                minEnergy = maxEnergy * 0.5f
                energy = minEnergy
                rate = parentConsumer.rate * parentGenetic.dna.energy.falloff * lerp(0.9f, 1.1f, Math.random().toFloat())
            }
        }
        parentBranch.children.add(newBranch)
        return newBranch
    }

    /**
     * Returns a vector with the direction pointing along the branch.
     */
    fun getDirectionVectorAlongBranch(length: Float, rotation: Float): Vector2 {
        return vec2(
            length * Math.cos(rotation.toDouble()).toFloat(),
            length * Math.sin(rotation.toDouble()).toFloat()
        )
    }

    /**
     * Calculates the position of a child from the parent's position.
     */
    fun getChildPosition(parentPos: Position, parentBranch: Branch, positionAlongBranch: Float = 0.94f): Vector2 {
        val dir = getDirectionVectorAlongBranch(parentBranch.length, parentBranch.rotation)
        return parentPos.position.cpy().add(dir.scl(positionAlongBranch))
    }

    /**
     * Handles the growth of branches for a specific entity.
     */
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

    /**
     * Handles the growth of the length for a specific entity.
     */
    fun growLength(entity: Entity) {
        val branch = branches.get(entity)
        val lengthGene = genetics.get(entity).dna.length
        branch.length += lengthGene.growthSpeed
    }

    /**
     * Handles the growth of leafs for a specific entity.
     */
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
                parent = entity
            }
            with<Consumer> {
                maxEnergy = leafsGene.maxEnergy
                rate = leafsGene.upkeep
            }
            with<Health> {
                current = leafsGene.maxHealth
                max = leafsGene.maxHealth
            }
            with<Age> {}
            with<Decompose> {
                max = 7f
                speed = 0.001f * Math.random().toFloat() * 5f
            }
            with<Movable> {
                weight = 100f
            }
        })
    }

    /**
     * Calculates the maximum of depth of the (informatical) tree for the given entity.
     * If the entity is of generation `2` and has children nested 2 levels deep this function will
     * return `4`.
     */
    fun getMaxGeneration(entity: Entity): Int {
        if (!branches.has(entity)) {
            return 0
        }
        val branch = branches.get(entity)
        return maxOf(branch.children.map { getMaxGeneration(it) }.max() ?: 0, branch.generation)
    }

    /**
     * Calculates the maximum amount of leafs for a specific entity based on the length, dna and
     * depth.
     */
    fun maxLeafCount(entity: Entity): Int {
        val branch = branches.get(entity)
        val dna = genetics.get(entity).dna
        val relativeDepth = getMaxGeneration(entity) - branch.generation
        return Math.floor(Math.pow(dna.leafs.leafCountFalloff.toDouble(), relativeDepth.toDouble()) *
            dna.leafs.maxGenerationLeafCountPerLength * branch.length).toInt()
    }

    fun life() {
        val consumerEntities = engine.entities
            .filter { consumers.has(it) && (!healths.has(it) || healths.get(it).alive) }

        for (entity in consumerEntities) {
            if (!branches.has(entity) || !genetics.has(entity) || !consumers.has(entity)) {
                continue
            }
            val branch = branches.get(entity)
            val dna = genetics.get(entity).dna
            val consumer = consumers.get(entity)

            var surplus = maxOf(consumer.energy - consumer.minEnergy, 0f)
            // 1. Growing leafs
            val canGrowLeaf =
                branch.children.filter { leafs.has(it) }.count() < maxLeafCount(entity) &&
                surplus >= dna.leafs.leafCost
            if (canGrowLeaf) {
                growLeaf(entity)
                consumer.energy -= dna.leafs.leafCost
                surplus -= dna.leafs.leafCost
            }
            // 2. Branching
            val canGrowBranches = 
                branch.children.filter { branches.has(it) }.count() == 0 &&
                branch.length > dna.branching.minLength * branch.maxLength &&
                branch.generation < dna.branching.maxDepth &&
                surplus >= dna.branching.branchCost
            if (canGrowBranches) {
                growBranches(entity)
                surplus -= dna.branching.branchCost
                consumer.energy -= dna.branching.branchCost
            }
            // 3. Growing length
            val canGrowLength = 
                branch.length < branch.maxLength &&
                surplus >= dna.length.growCost
            if (canGrowLength) {
                growLength(entity)
                surplus -= dna.length.growCost
                consumer.energy -= dna.length.growCost
            }
        }
        // Kill all obsolete leafs.
        for (entity in consumerEntities) {
            if (branches.has(entity)) {
                val branch = branches.get(entity)
                val livingLeafs = branch.children.filter { leafs.has(it) && healths.has(it) && healths.get(it).alive }
                val maxLeafs = maxLeafCount(entity)
                if (livingLeafs.count() > maxLeafs) {
                    for (i in maxLeafs..livingLeafs.size - 1) {
                        // Start losing all inputted (even though energy is put in), resulting in eventual death.
                        consumers.get(livingLeafs[i]).effectiveness = 0f
                    }
                }
            }
        }
    }

    /**
     * After all branches changed size and rotations the branches need to be readjusted.
     * This function recursively loops over all branches and fixes their positions after
     * each tick's update.
     */
    fun adjust(entity: Entity?, newPos: Vector2? = null) {
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

    override fun tick(tick: Int) {
        life()
        for (entity in engine.entities) {
            if (roots.has(entity)) {
                adjust(entity)
            }
        }
    }
}
