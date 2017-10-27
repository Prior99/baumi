package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine
import de.cronosx.baumi.component.Leaf
import de.cronosx.baumi.component.Movable
import de.cronosx.baumi.data.*
import ktx.ashley.mapperFor
import ktx.log.*
import java.util.*
import kotlin.system.measureTimeMillis

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

class Ticker() : ReplayIntervalSystem(config.tickSpeed) {
    var subSystems: List<TickSubSystem> = ArrayList()
    val movables = mapperFor<Movable>()
    val leafs = mapperFor<Leaf>()

    override fun addedToEngine(engine: Engine) {
        subSystems = listOf(
            Buffering(engine), // Should be above `EnergyDistribution`.
            // Death(engine),
            Aging(engine),
            Decomposing(engine),
            Leafs(engine),
            Growth(engine),
            EnergyDistribution(engine),
            Fruits(engine)
        )
    }

    override fun updateInterval() {
        val time = measureTimeMillis {
            world.tick++
            for (system in subSystems) {
                system.tick(world.tick)
            }
        }.toDouble() / 1000.0
        debug { "Calculated tick ${world.tick} in ${time.format(2)}s." }
        if (time > 1f / config.tickSpeed) {
            error { "Couldn't keep up! Tick took ${time.format(2)}s!" }
        }
    }
}
