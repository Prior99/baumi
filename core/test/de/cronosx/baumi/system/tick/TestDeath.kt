package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestDeath : Spek({
    var engine = PooledEngine()

    val healths = mapperFor<Health>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Death system") {
        var death: Death? = null

        beforeEachTest {
            death = Death(engine)
        }

        it("kills all children of a dead entity") {
            engine.entity{
                with<Health>{
                    current = 0f
                    max = 1f
                }
                with<Parent>{
                    children = mutableListOf(
                        engine.entity{
                            with<Health>{
                                current = 1f
                                max = 1f
                            }
                            with<Parent>{
                                children = mutableListOf(
                                    engine.entity{
                                        with<Health>{
                                            current = 1f
                                            max = 1f
                                        }
                                        with<Parent>{ }
                                    }
                                )
                            }
                        },
                        engine.entity{
                            with<Health>{
                                current = 1f
                                max = 1f
                            }
                            with<Branch>{ }
                        }
                    )
                }
            }
            death!!.tick(1)
            expect(engine.entities.filter{ healths.get(it).alive }.count()).equal(0)
        }
    }
})

