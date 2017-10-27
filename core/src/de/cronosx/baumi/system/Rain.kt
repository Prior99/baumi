package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import de.cronosx.baumi.Bus.on
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.data.*
import de.cronosx.baumi.events.Drag
import de.cronosx.baumi.events.DragStart
import de.cronosx.baumi.events.DragStop
import ktx.ashley.*
import ktx.math.*

class Rain() : EntitySystem() {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()
    val rainDrops = mapperFor<RainDrop>()
    val buffers = mapperFor<Buffer>()
    val groundWaters = mapperFor<GroundWater>()
    val movables = mapperFor<Movable>()

    var timeContingent = 0f
    val size = vec2(500f, 250f)
    var current: Entity? = null
    var movementDelta: Vector2 = vec2(0f, 0f)

    init {
        on { event: DragStart ->
            val entity = event.entity;
            if (!clouds.has(entity) || !movables.has(entity)) {
                return@on
            }
            val movable = movables.get(entity)
            movable.fixed = true
            current = entity
        }

        on { event: DragStop ->
            val entity = event.entity;
            if (!clouds.has(entity) || !movables.has(entity)) {
                return@on
            }
            val movable = movables.get(entity)
            movable.fixed = false
            current = null
        }

        on { event: Drag ->
            val entity = event.entity;
            if (entity != current) {
                return@on
            }
            val position = positions.get(event.entity).position
            position.y = maxOf(position.y, config.cloudHeight)
            movementDelta += event.delta
        }
    }

    fun spawn(delta: Float) {
        if (current == null || movementDelta == null) {
            return
        }
        // Increase amount of unspent time by time passed.
        timeContingent += delta
        // Current position of the cloud.
        val cloudPosition = positions.get(current!!).position.cpy()
        // Calculate the amount of pixels the cloud moved since the last call.
        // The amount of drops possible to spawn by time.
        val dropsPerTime = FloatMath.floor(timeContingent * config.dropsPerSecond)
        // Decrease the time contingent by the spent time.
        timeContingent -= dropsPerTime / config.dropsPerSecond
        // Increase the amount of drops to spawn according to the amount of pixels moved.
        val movedModifier = if (cloudPosition.y == config.cloudHeight) 0f else delta * movementDelta!!.len() * 20f
        val dropsToSpawn = dropsPerTime * movedModifier
        // Spawn drops.
        for(i in 0 until dropsToSpawn.toInt()) {
            val dropPosition = vec2(cloudPosition.x + Math.random().toFloat() * size.x, cloudPosition.y + size.y / 2f)
            engine.entity {
                with<Position>{ position = dropPosition }
                with<RainDrop>{ index = Math.floor(Math.random() * 4.0).toInt() }
                with<Movable>{
                    weight = 500f
                    fixed = false
                    floating = false
                }
            }
        }
        val cloud = clouds.get(current!!)
        cloud.content -= dropsToSpawn * config.dropContent
        movementDelta = vec2(0f, 0f)
    }

    fun rain() {
        val hitRainDrops = engine.entities
                .filter { rainDrops.has(it) }
                .filter { positions.get(it).position.y <= config.groundHeight }
        val addBufferContent = hitRainDrops.count() * config.dropContent
        val buffer = buffers.get(engine.entities.find { groundWaters.has(it) })
        buffer.add(addBufferContent)
        hitRainDrops.forEach{ engine.removeEntity(it) }
    }

    override fun update(delta: Float) {
        // Cloud could have been deleted.
        if (current != null && (!clouds.has(current!!) || !positions.has(current!!))) {
            current = null
        }
        // Spawn raindrops.
        spawn(delta)
        // Handle existing drops.
        rain()
    }
}