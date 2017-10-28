package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.math.vec2
import ktx.log.*

/**
 * `lerp` is a linear interpolation method, adjusting `a` to move towards `b` with the
 * speed of `f`.
 */
fun lerp(a: Float, b: Float, f: Float): Float {
    return a + (b - a) * f
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

class Growth(engine: Engine) : TickSubSystem(engine) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val fruits = mapperFor<Fruit>()
    val positions = mapperFor<Position>()
    val roots = mapperFor<Root>()
    val ages = mapperFor<Age>()
    val parents = mapperFor<Parent>()
    val children = mapperFor<Child>()

    /**
     * Create a new branch forked from the `parent` entity with the given rotations.
     */
    fun createBranch(parentEntity: Entity, rotationOffsetFixed: Float, rotationOffsetSpread: Float): Entity {
        val parentBranch = branches.get(parentEntity)
        val parentChild = children.get(parentEntity)
        val parentGenetic = genetics.get(parentEntity)
        val parentHealth = healths.get(parentEntity)
        val parentConsumer = consumers.get(parentEntity)
        val parentParent = parents.get(parentEntity)

        val rotationOffset = rotationOffsetFixed + lerp(-rotationOffsetSpread, rotationOffsetSpread, Math.random().toFloat())
        val newMaxLength =
            parentGenetic.dna.length.falloff *
            parentBranch.maxLength *
            lerp(0.9f, 1.1f, Math.random().toFloat())

        val newBranch = engine.entity {
            with<Position> {} // Will be adjusted anyway.
            with<Branch> {
                rotation = parentBranch.rotation + rotationOffset
                maxLength = newMaxLength
            }
            with<Parent> {
                children = ArrayList()
            }
            with<Child> {
                generation = parentChild.generation + 1
                parent = null
                positionAlongParent = 0f

            }
            with<Genetic> {
                dna = parentGenetic.dna
            }
            with<Health> {
                max = parentHealth.max * parentGenetic.dna.health.falloff
                current = max
            }
            with<Consumer> {
                maxEnergy = parentConsumer.maxEnergy * parentGenetic.dna.energy.falloff * lerp(0.8f, 1.5f, Math.random().toFloat())
                minEnergy = maxEnergy / 2f
                energy = minEnergy
                rate = parentConsumer.rate * parentGenetic.dna.energy.falloff * lerp(0.8f, 1.5f, Math.random().toFloat())
            }
        }
        parentParent.children.add(newBranch)
        return newBranch
    }

    /**
     * Handles the growth of branches for a specific entity.
     */
    fun growBranches(parent: Entity) {
        val branchingGene = genetics.get(parent).dna.branching

        val pi = Math.PI.toFloat()
        // Sometimes, we also want to create a third branch in the middle. This is determined
        // by the `tripleProbability`.
        val doTriple = Math.random() < branchingGene.tripleProbability
        val spacing = if (doTriple) branchingGene.rotationSpacing / 2f else branchingGene.rotationSpacing
        // create left branch
        createBranch(parent, -spacing * pi, branchingGene.rotationVariety * pi)
        // create right branch
        createBranch(parent, spacing * pi, branchingGene.rotationVariety * pi)
        if (doTriple) {
            createBranch(parent, 0f, branchingGene.rotationVariety * pi)
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

    fun growFruit(entity: Entity) {
        val branch = branches.get(entity)
        val parentComponent = parents.get(entity)
        val childComponent = children.get(entity)
        val fruitsGene = genetics.get(entity).dna.fruits
        val parentPosition = positions.get(entity).position
        val parentGenetic = genetics.get(entity)

        val randomPositionAlongBranch = Math.random().toFloat()
        val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
        parentComponent.children.add(engine.entity {
            with<Position> {
                position = dir.cpy().scl(randomPositionAlongBranch) + parentPosition
            }
            with<Fruit> {
                rotation = Math.random().toFloat() * Math.PI.toFloat() * 2f
                age = 0
            }
            with<Child> {
                generation = childComponent.generation + 1
                positionAlongParent = randomPositionAlongBranch
                parent = entity
            }
            with<Consumer> {
                maxEnergy = fruitsGene.maxEnergy
                rate = fruitsGene.upkeep
            }
            with<Movable> {
                weight = 300f
                fixed = true
            }
            with<Genetic> {
                dna = parentGenetic.dna
            }
        })
    }

    /**
     * Handles the growth of leafs for a specific entity.
     */
    fun growLeaf(entity: Entity) {
        val branch = branches.get(entity)
        val parentComponent = parents.get(entity)
        val childComponent = children.get(entity)
        val leafsGene = genetics.get(entity).dna.leafs
        val parentPosition = positions.get(entity).position

        val randomPositionAlongBranch = Math.random().toFloat()
        val rotationOffset = Math.random().toFloat() * leafsGene.maxRotationOffset * 2f - leafsGene.maxRotationOffset
        val dir = getDirectionVectorAlongBranch(branch.length, branch.rotation)
        parentComponent.children.add(engine.entity {
            with<Position> {
                position = dir.cpy().scl(randomPositionAlongBranch) + parentPosition
            }
            with<Leaf> {
                rotation = branch.rotation + rotationOffset * Math.PI.toFloat()
            }
            with<Child> {
                generation = childComponent.generation + 1
                positionAlongParent = randomPositionAlongBranch
                parent = entity
            }
            with<Consumer> {
                maxEnergy = leafsGene.maxEnergy * lerp(0.9f, 1.2f, Math.random().toFloat())
                minEnergy = maxEnergy * 0.5f
                rate = leafsGene.upkeep * lerp(0.9f, 1.2f, Math.random().toFloat())
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
                fixed = true
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
        val parent = parents.get(entity)
        val child = children.get(entity)
        return maxOf(parent.children.map { getMaxGeneration(it) }.max() ?: 0, child.generation)
    }

    fun maxFruitCount(entity: Entity): Int {
        val branch = branches.get(entity)
        val dna = genetics.get(entity).dna
        return Math.floor(dna.fruits.maxGenerationFruitCountPerLength * branch.length.toDouble()).toInt()
    }

    /**
     * Calculates the maximum amount of leafs for a specific entity based on the length, dna and
     * depth.
     */
    fun maxLeafCount(entity: Entity): Int {
        val branch = branches.get(entity)
        val child = children.get(entity)
        val dna = genetics.get(entity).dna
        val relativeDepth = getMaxGeneration(entity) - child.generation
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
            val parent = parents.get(entity)
            val child = children.get(entity)
            val dna = genetics.get(entity).dna
            val consumer = consumers.get(entity)

            var surplus = maxOf(consumer.energy - consumer.minEnergy, 0f)
            // 1. Growing leafs
            val canGrowLeaf =
                parent.children.filter { leafs.has(it) }.count() < maxLeafCount(entity) &&
                surplus >= dna.leafs.leafCost &&
                parent.children.filter { leafs.has(it) && ages.get(it).age <= 70 }.count() < dna.leafs.maxYoungLeafs
            if (canGrowLeaf) {
                growLeaf(entity)
                consumer.energy -= dna.leafs.leafCost
                surplus -= dna.leafs.leafCost
            }
            // 2. Branching
            val canGrowBranches =
                parent.children.filter { branches.has(it) }.count() == 0 &&
                branch.length > dna.branching.minLength * branch.maxLength &&
                child.generation < dna.branching.maxDepth &&
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
            // 4. Growing fruits
            val canGrowFruit =
                parent.children.filter { fruits.has(it) }.count() < maxFruitCount(entity) &&
                surplus >= dna.fruits.fruitCost &&
                child.generation >= dna.fruits.minGeneration &&
                parent.children.filter { branches.has(it) }.count() == 0
            if (canGrowFruit) {
                growFruit(entity)
                consumer.energy -= dna.fruits.fruitCost
                surplus -= dna.fruits.fruitCost
            }
        }
        // Kill all obsolete leafs.
        for (entity in consumerEntities) {
            if (branches.has(entity)) {
                val parent = parents.get(entity)
                val livingLeafs = parent.children.filter { leafs.has(it) && healths.has(it) && healths.get(it).alive }
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
        val parent = parents.get(entity)
        for (child in parent.children) {
            if (children.has(child)) {
                val childComponent = children.get(child)
                var childPosition = positions.get(child)
                childPosition.position = getChildPosition(position, branch, childComponent.positionAlongParent)
            } else {
                adjust(child, getChildPosition(position, branch))
            }
        }
    }

    override fun tick(number: Int) {
        life()
        engine.entities
                .filter { roots.has(it) }
                .forEach { adjust(it) }
    }
}