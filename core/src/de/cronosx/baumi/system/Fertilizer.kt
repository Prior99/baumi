package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import de.cronosx.baumi.Bus.on
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.appHeight
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.config
import de.cronosx.baumi.data.world
import de.cronosx.baumi.events.Drag
import de.cronosx.baumi.events.DragStart
import de.cronosx.baumi.events.DragStop
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.log.debug
import ktx.math.vec2

class Fertilizer() : EntitySystem() {
    val fertilizerBags = mapperFor<FertilizerBag>()
    val positions = mapperFor<Position>()
    val fertilizerGrains = mapperFor<FertilizerGrain>()
    val buffers = mapperFor<Buffer>()
    val carts = mapperFor<Cart>()
    val movables = mapperFor<Movable>()
    var currentBag: Entity? = null
    var moveDelta = 0f
    val spawnMoveInterval = 70f
    val bagSpawnInterval = 0.5f
    var bagTime = 0f

    init {
        on { event: DragStart ->
            if (fertilizerBags.has(event.entity)) {
                movables.get(event.entity).fixed = true
                currentBag = event.entity
                moveDelta = 0f
            }
        }
        on { event: DragStop ->
            if (fertilizerBags.has(event.entity)) {
                movables.get(event.entity).fixed = false
                currentBag = event.entity
                val position = positions.get(event.entity).position
                position.y = maxOf(config.groundHeight, position.y)
            }
        }
        on { event: Drag ->
            if (currentBag == event.entity) {
                moveDelta += event.delta.len()
            }
        }
    }

    override fun update(delta: Float) {
        if (currentBag != null && fertilizerBags.has(currentBag)) {
            val bagPosition = positions.get(currentBag).position
            val bag = fertilizerBags.get(currentBag)
            moveDelta += delta
            if (moveDelta > spawnMoveInterval && bagPosition.y < config.groundHeight) {
                moveDelta = 0f
                bag.content--
                engine.entity {
                    with<Position>{ position = bagPosition.cpy() }
                    with<FertilizerGrain>{
                        index = (Math.random() * 4).toInt()
                        rotation = Math.random().toFloat() * 2f * FloatMath.PI
                    }
                    with<Buffer> {
                        max = config.maxFertilizerBuffer
                        current = config.maxFertilizerBuffer
                        energyYield = config.fertilizerYield
                    }
                    with<Producer> {
                        rate = 0f
                    }
                }
            }
        }
        engine.entities
                .filter { fertilizerBags.has(it) && fertilizerBags.get(it).content <= 0 }
                .forEach{ engine.removeEntity(it) }
        engine.entities
                .filter { fertilizerGrains.has(it) && buffers.get(it).current <= 0 }
                .forEach{ engine.removeEntity(it) }
        bagTime += delta
        if (bagTime > bagSpawnInterval) {
            bagTime = 0f
            engine.entities
                    .filter { carts.has(it) && positions.get(it).position.x < -100 }
                    .filter { carts.get(it).content > config.fertilizerCost }
                    .forEach {
                        carts.get(it).content -= config.fertilizerCost
                        engine.entity{
                            with<Position>{
                                position = vec2(Math.random().toFloat() * (1080 - 56), config.groundHeight)
                            }
                            with<FertilizerBag>{ content = config.maxFertilizerContent }
                            with<Movable> {
                                fixed = false
                                floating = false
                                weight = 1000f
                            }
                            with<Draggable> {
                                size = vec2(195f, 51f)
                            }
                        }
                    }
        }

    }
}
