package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import de.cronosx.baumi.test.EmptyApplication
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestEnergyDistribution : Spek({
    var engine = PooledEngine()

    val consumers = mapperFor<Consumer>()
    val healths = mapperFor<Health>()

    beforeEachTest {
        HeadlessApplication(EmptyApplication())
        engine = PooledEngine()
    }

    afterEachTest {
        Gdx.app.exit()
    }

    describe("The EnergyDistribution system") {
        var energyDistribution: EnergyDistribution? = null

        beforeEachTest {
            energyDistribution = EnergyDistribution(engine)
        }

        it("distributes the surplus across all producers equally until the `maxEnergy`") {
            engine.entity {
                with<Producer>{
                    rate = 3f
                }
            }
            engine.entity {
                with<Producer>{
                    rate = 17f
                }
            }
            for (i in 1 .. 4) {
                engine.entity {
                    with<Consumer>{
                        rate = 2f
                        effectiveness = 0.5f
                        maxEnergy = 10f
                    }
                    with<Health>{
                        max = 100f
                        current = 100f
                    }
                }
            }
            energyDistribution!!.tick(1)
            val consumerEntities = engine.entities .filter { consumers.has(it) }
            consumerEntities
                    .map{ consumers.get(it) }
                    .forEach{ expect(it.energy ).equal(0.5f) }
            consumerEntities
                    .map{ healths.get(it) }
                    .forEach{ expect(it.current).equal(100f) }
            for (i in 1 .. 9) {
                energyDistribution!!.tick(i + 1)
            }
            consumerEntities
                    .map{ consumers.get(it) }
                    .forEach{ expect(it.energy ).equal(5f) }
            for (i in 1 .. 20) {
                energyDistribution!!.tick(i + 10)
            }
            consumerEntities
                    .map{ consumers.get(it) }
                    .forEach{ expect(it.energy ).equal(10f) }
        }

        it("distributes the health loss across all consumers if not enough energy is generated") {
            engine.entity {
                with<Producer>{
                    rate = 6f
                }
            }
            engine.entity {
                with<Producer>{
                    rate = 4f
                }
            }
            for (i in 1 .. 4) {
                engine.entity {
                    with<Consumer>{
                        rate = 5f
                        effectiveness = 0.5f
                        maxEnergy = 10f
                        healthDecayRate = 0.1f
                    }
                    with<Health>{
                        max = 100f
                        current = 100f
                    }
                }
            }
            energyDistribution!!.tick(1)
            val consumerEntities = engine.entities .filter { consumers.has(it) }
            consumerEntities
                    .map{ consumers.get(it) }
                    .forEach{ expect(it.energy ).equal(0.0f) }
            consumerEntities
                    .map{ healths.get(it) }
                    .forEach{ expect(it.current).equal(90f) }
            for (i in 1 .. 9) {
                energyDistribution!!.tick(i + 1)
            }
            consumerEntities
                    .map{ healths.get(it) }
                    .forEach{ expect(it.alive ).equal(false) }
        }
    }
})

