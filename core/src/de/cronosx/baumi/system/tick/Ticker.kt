package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import ktx.math.plus
import ktx.log.*
import kotlin.system.measureTimeMillis

abstract class CustomIntervalSystem(var interval: Float) : EntitySystem(0) {
    var accumulator = 0f;

    override fun update (deltaTime: Float) {
        accumulator += deltaTime;

        while (accumulator >= interval) {
            accumulator -= interval;
            updateInterval();
        }
    }

    abstract fun updateInterval();
}

class Ticker() : CustomIntervalSystem(world.tickSpeed) {
    var subSystems: List<TickSubSystem> = ArrayList()
    var tick = 0

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

    override fun update(delta: Float) {
        super.update(if (debug.extremeSpeed) 100f * delta else delta)
    }

    override fun updateInterval() {
        val time = measureTimeMillis {
            tick++
            for (system in subSystems) {
                system.tick(tick)
            }
        }
        if (time > 1 / world.tickSpeed) {
            error { "Couldn't keep up! Tick took ${time}ms!" }
        }
    }
}
