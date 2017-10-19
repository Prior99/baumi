package de.cronosx.baumi.system

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*
import ktx.math.*

data class CurrentCloud(
        val cloud: Entity,
        val offsetToCursor: Vector2
)

class Rain() : EntitySystem() {
    val clouds = mapperFor<Cloud>()
    val positions = mapperFor<Position>()
    val rainDrops = mapperFor<RainDrop>()
    val buffers = mapperFor<Buffer>()
    val groundWaters = mapperFor<GroundWater>()
    val movables = mapperFor<Movable>()

    var timeContingent = 0f
    val size = vec2(500f, 250f)
    var current: CurrentCloud? = null
    var lastPosition: Vector2? = null

    fun touchDown(touchPosition: Vector2) {
        val cloud = engine.entities
            .filter{ clouds.has(it) && positions.has(it) }
            .find{
                val position = positions.get(it).position.cpy()
                touchPosition >= position && touchPosition <= position + size
            }
        if (cloud != null) {
            // Reset time.
            timeContingent = 0f
            val movable = movables.get(cloud)
            val cloudPosition = positions.get(cloud).position
            // Make cloud not be influenced by wind anymore.
            movable.fixed = true
            // Store cloud and the offset to the cursor.
            current = CurrentCloud(
                    cloud,
                    offsetToCursor = touchPosition - cloudPosition
            )
            // Store the position of the cloud.
            lastPosition = cloudPosition.cpy()
        }
    }

    fun touchUp() {
        if (current != null) {
            val movable = movables.get(current!!.cloud)
            movable.fixed = false
            current = null
        }
    }

    fun touchDragged(touchPosition: Vector2) {
        if (current == null) {
            return
        }
        val position = positions.get(current!!.cloud)
        val actualPosition = touchPosition - current!!.offsetToCursor
        position.position = vec2(actualPosition.x, maxOf(actualPosition.y, config.cloudHeight))
    }

    fun spawn(delta: Float) {
        if (current == null) {
            return
        }
        // Increase amount of unspent time by time passed.
        timeContingent += delta
        // Current position of the cloud.
        val cloudPosition = positions.get(current!!.cloud).position.cpy()
        // Calculate the amount of pixels the cloud moved since the last call.
        val moved = (lastPosition!! - cloudPosition).len()
        // The amount of drops possible to spawn by time.
        val dropsPerTime = FloatMath.floor(timeContingent * config.dropsPerSecond)
        // Decrease the time contingent by the spent time.
        timeContingent -= dropsPerTime / config.dropsPerSecond
        // Increase the amount of drops to spawn according to the amount of pixels moved.
        val movedModifier = delta * moved * 20f
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
        val cloud = clouds.get(current!!.cloud)
        cloud.content -= dropsToSpawn * config.dropContent
        lastPosition = cloudPosition.cpy()
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
        if (current != null && (!clouds.has(current!!.cloud) || !positions.has(current!!.cloud))) {
            current = null
        }
        // Spawn raindrops.
        spawn(delta)
        // Handle existjng drops.
        rain()
    }
}