package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.defaultDna
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestGrowth : Spek({
    var engine = PooledEngine()

    val decomposes = mapperFor<Decompose>()
    val healths = mapperFor<Health>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Growth system") {
        var growth: Growth? = null

        beforeEachTest {
            growth = Growth(engine)
            engine.entity {
                with<Position> {
                    position = vec2(540f, 360f)
                }
                with<Branch> {
                    rotation = defaultDna.rotation.initial
                    length = defaultDna.length.initial
                    maxLength = defaultDna.length.max
                    children = ArrayList()
                }
                with<Genetic> {
                    dna = defaultDna
                }
                with<Health> {
                    max = defaultDna.health.max
                    current = defaultDna.health.max
                }
                with<Consumer> {
                    maxEnergy = defaultDna.energy.max
                    minEnergy = maxEnergy / 2f
                    energy = minEnergy
                    rate = defaultDna.energy.upkeep
                }
                with<Root> {}
            }
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
            growth!!.tick(1)
            expect(decomposes.get(entity).current).equal(1f)
            growth!!.tick(2)
            expect(decomposes.get(entity).current).equal(2f)
            growth!!.tick(3)
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
            growth!!.tick(1)
            expect(decomposes.get(entity).current).equal(0f)
        }
    }
})

