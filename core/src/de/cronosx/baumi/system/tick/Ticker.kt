package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import ktx.math.plus
import ktx.log.*
import kotlin.system.measureTimeMillis

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

abstract class ReplayIntervalSystem(var interval: Float) : EntitySystem(0) {
    override fun update(_delta: Float) {
        val now = System.currentTimeMillis().toDouble() / 1000.0;
        var timePassed = now - world.lastTick
        val ticksToCalculate = Math.floor(timePassed / interval).toInt()
        if (ticksToCalculate > 1) {
            info { "Calculating $ticksToCalculate ticks." }
        }
        val calculationTime = measureTimeMillis {
            while (timePassed >= interval) {
                timePassed -= interval
                updateInterval();
                world.lastTick = now
            }
        }.toDouble() / 1000.0
        if (calculationTime > 1f / config.tickSpeed || ticksToCalculate > 1) {
            info { "Calculating of $ticksToCalculate tick(s) took ${calculationTime.format(2)}s." }
        }
    }

    abstract fun updateInterval();
}

class Ticker() : ReplayIntervalSystem(1 / config.tickSpeed) {
    var subSystems: List<TickSubSystem> = ArrayList()

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
            world.tick++
            for (system in subSystems) {
                system.tick(world.tick)
            }
        }.toDouble() / 1000.0
        if (time > 1f / config.tickSpeed) {
            error { "Couldn't keep up! Tick took ${time.format(2)}s!" }
        }
    }
}
