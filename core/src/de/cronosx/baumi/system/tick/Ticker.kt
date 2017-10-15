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
    var replaying = false
    var totalReplayTicks = 0
    var replayed = 0

    override fun update(_delta: Float) {
        val now = System.currentTimeMillis().toDouble() / 1000.0
        // The amount of seconds passed since the last calculated tick.
        var timePassed = now - world.lastTick
        val ticksToCalculate = Math.floor(timePassed / interval).toInt()
        // If more than `config.maxTicksPerInterval` tick have to be calculated this call will not manage to
        // Get the world up-to-date with the ticks and will be needed to be called again. Indicate that to
        // the outer world by setting `replaying` to `true`.
        if (ticksToCalculate > config.maxTicksPerInterval) {
            if (!replaying) {
                replaying = true
                totalReplayTicks = ticksToCalculate
                info { "Replay necessary. Starting to replay $totalReplayTicks ticks." }
            }
            replayed = totalReplayTicks - ticksToCalculate
            info { "Calculating $ticksToCalculate ticks. $replayed/$totalReplayTicks ticks already calculated." }
        // If the amount of ticks to calculate can be managed in this one call there is no need to show a progress
        // bar or anything, or we are done with replaying and want to hide it now.
        } else {
            replaying = false
            totalReplayTicks = 0
            replayed = 0
        }
        // The amount of ticks calculated in this call.
        var ticked = 0
        // The time it took to calculate all ticks.
        val calculationTime = measureTimeMillis {
            // This loop will loop with the following conditions:
            // 1. The time which has passed since the last time `update()` has been called is greater than `interval`.
            //    This ensures that the loop will iterate all the ticks which would have been calculated since the
            //    last call to `update()`. So if 100 seconds have passed (because the app was closed or the device
            //    was really slow and there are 2 ticks per second, this loop will be executed 200 times.
            // 2. If the debug options have activated `extremeSpeed` then this loop will just execute once the
            //    `update()` has been called. Because of this the ticking is as fast as possible without interrupting
            //    other calculations.
            // 3. The loop may never tick more often than `config.maxTicksPerInterval` in order to keep the
            //    app and device responsive.
            while ((timePassed >= interval || (debug.extremeSpeed && ticked == 0)) && ticked < config.maxTicksPerInterval) {
                timePassed -= interval
                updateInterval()
                ticked++
            }
            if (ticked > 0) {
                // Increase the time of the last tick by the amount of ticked ticks multiplied by the tick interval.
                // If the tick interval is 2 (=two ticks per second) and 100 ticks have been calculated the time of
                // the last calculated tick needs to be increased by 50 seconds, because
                // (1 second / 2 ticks) * 100 ticks = 50 seconds.
                world.lastTick += (1.0 / config.tickSpeed) * ticked
            }
        }.toDouble() / 1000.0
        // Info how long it took to calculate all ticks.
        if (calculationTime > 1f / config.tickSpeed || ticksToCalculate > 1) {
            info { "Calculating of $ticked tick(s) took ${calculationTime.format(2)}s." }
        }
    }

    abstract fun updateInterval()
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
