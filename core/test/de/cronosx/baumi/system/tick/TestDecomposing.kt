package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestDecomposing : Spek({
    var engine = PooledEngine()

    val decomposes = mapperFor<Decompose>()
    val healths = mapperFor<Health>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Decomposing system") {
        var decomposing: Decomposing? = null

        beforeEachTest {
            decomposing = Decomposing(engine)
        }

        it("increases the decomposition value of all dead entities towards their max") {
            val entity = engine.entity{
                with<Health>{
                    current = 0f
                    max = 1f
                }
                with<Decompose> {
                    current = 0f
                    speed = 1f
                    max = 2f
                }
            }
            decomposing!!.tick(1)
            expect(decomposes.get(entity).current).equal(1f)
            decomposing!!.tick(2)
            expect(decomposes.get(entity).current).equal(2f)
            decomposing!!.tick(3)
            expect(decomposes.get(entity).current).equal(2f)
        }

        it("ignores living entities") {
            val entity = engine.entity{
                with<Health>{
                    current = 1f
                    max = 1f
                }
                with<Decompose> {
                    current = 0f
                    speed = 1f
                    max = 2f
                }
            }
            decomposing!!.tick(1)
            expect(decomposes.get(entity).current).equal(0f)
        }
    }
})

