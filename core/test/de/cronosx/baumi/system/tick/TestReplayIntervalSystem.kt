package de.cronosx.baumi.system.tick

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.winterbe.expekt.expect
import de.cronosx.baumi.data.config
import de.cronosx.baumi.data.world
import de.cronosx.baumi.test.EmptyApplication
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class TestReplayIntervalSystem : Spek({
    var engine = PooledEngine()

    beforeEachTest {
        HeadlessApplication(EmptyApplication())
        engine = PooledEngine()
    }

    afterEachTest {
        Gdx.app.exit()
    }

    describe("The abstract ReplayInterval system") {

        class TestSystem(calendar: Calendar) : ReplayIntervalSystem(1f, calendar) {
            var called = 0
            override fun updateInterval() {
                called++
            }
        }

        it("ticks only once if called multiple times") {
            val calendar = Calendar.getInstance()
            val system = TestSystem(calendar)
            world.lastTick = calendar.timeInMillis.toDouble() / 1000.0
            calendar.add(Calendar.SECOND, 1)
            engine.addSystem(system)
            for (i in 0 .. 10) {
                engine.update(1.0f)
            }
            expect(system.called).equal(1)
        }

        it("replays if a long period of time has passed") {
            val calendar = Calendar.getInstance()
            world.lastTick = calendar.timeInMillis.toDouble() / 1000.0
            calendar.add(Calendar.SECOND, 1000)
            val system = TestSystem(calendar)
            engine.addSystem(system)
            for (i in 1 .. 1000 / config.maxTicksPerInterval) {
                engine.update(1.0f)
                expect(system.called).equal(i * config.maxTicksPerInterval)
                if (i != 1000 / config.maxTicksPerInterval) {
                    expect(system.replaying).equal(true)
                    expect(system.totalReplayTicks).equal(1000)
                    expect(system.replayed).equal((i - 1) * config.maxTicksPerInterval)
                }
            }
            expect(system.called).equal(1000)
            expect(world.lastTick).equal(calendar.timeInMillis.toDouble() / 1000.0)
        }
    }
})

