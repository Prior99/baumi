package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.debug
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestBuffering : Spek({
    var engine = PooledEngine()

    val buffers = mapperFor<Buffer>()
    val producers = mapperFor<Producer>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Buffering system") {
        var buffering: Buffering? = null

        beforeEachTest {
            buffering = Buffering(engine)
        }

        it("sets the producer's rate to the buffer's `energyYield` and deduces the used energy") {
            val buffer = engine.entity{
                with<Producer>{
                    rate = 1f
                }
                with<Buffer>{
                    current = 100f
                    max = 100f
                    energyYield = 1f
                }
            }
            buffering!!.tick(1)
            expect(producers.get(buffer).rate).equal(1f)
            expect(buffers.get(buffer).current).equal(99f)
            buffering!!.tick(2)
            expect(producers.get(buffer).rate).equal(1f)
            expect(buffers.get(buffer).current).equal(98f)
        }

        describe("with the buffer being empty") {
            it("sets the producer's rate to 0") {
                val buffer = engine.entity{
                    with<Producer>{
                        rate = 1f
                    }
                    with<Buffer>{
                        current = 0f
                        max = 100f
                        energyYield = 1f
                    }
                }
                buffering!!.tick(1)
                expect(producers.get(buffer).rate).equal(0f)
            }
        }

        describe("with `debug.infiniteBuffers = true`") {
            beforeEachTest {
                debug.infiniteBuffers = true
            }

            afterEachTest {
                debug.infiniteBuffers = false
            }

            it("always produces at maximum capacity") {
                val buffer = engine.entity{
                    with<Producer>{
                        rate = 0f
                    }
                    with<Buffer>{
                        current = 0f
                        max = 100f
                        energyYield = 1f
                    }
                }
                buffering!!.tick(1)
                expect(buffers.get(buffer).current).equal(99f)
                expect(producers.get(buffer).rate).equal(1f)
            }
        }
    }
})

