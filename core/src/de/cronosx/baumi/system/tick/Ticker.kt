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

val tickSpeed = 0.01f

class Ticker() : IntervalSystem(tickSpeed) {
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()
    val positions = mapperFor<Position>()
    val producers = mapperFor<Producer>()

    var subSystems: List<TickSubSystem> = ArrayList()

    var root: Entity? = null
    var tick = 0

    override fun addedToEngine(engine: Engine) {
        subSystems = listOf(
            // Death(engine),
            Aging(engine),
            Decomposing(engine),
            Leafs(engine),
            Growth(engine),
            EnergyDistribution(engine)
        )
    }

    override fun updateInterval() {
        val time = measureTimeMillis {
            tick++
            for (system in subSystems) {
                system.tick(tick)
            }
        }
        if (time > 1 / tickSpeed) {
            error { "Couldn't keep up! Tick took ${time}ms!" }
        }
    }
}
