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

class TestFruits : Spek({
    var engine = PooledEngine()

    val fruits = mapperFor<Fruit>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Fruits system") {
        var fruitSystem: Fruits? = null

        beforeEachTest {
            fruitSystem = Fruits(engine)
            engine.entity {
                with<Cart> {}
                with<Position> {}
            }
        }

        it("increases the age of all underage fruits if energy is available") {
            val fruit = engine.entity{
                with<Consumer>{
                    rate = 1f
                    energy = 1f
                }
                with<Fruit>{
                    age = 0
                }
                with<Position> {
                    position = vec2(0f, 600f)
                }
                with<Genetic> {}
                with<Child> {
                    parent = engine.entity {
                        with<Parent>{}
                        with<Genetic>{}
                    }
                }
            }
            fruitSystem!!.tick(1)
            expect(fruits.get(fruit).age).equal(1)
        }

        it("ignores the age of all underage fruits if no energy is available") {
            val fruit = engine.entity{
                with<Consumer>{
                    rate = 1f
                    energy = 0f
                }
                with<Fruit> {
                    age = 0
                }
                with<Position> {
                    position = vec2(0f, 600f)
                }
                with<Genetic> {}
                with<Child> {
                    parent = engine.entity {
                        with<Parent>{}
                        with<Genetic>{}
                    }
                }
            }
            fruitSystem!!.tick(1)
            expect(fruits.get(fruit).age).equal(0)
        }

        it("removes the fruit if it is too old") {
            val fruit = engine.entity{
                with<Consumer>{
                    rate = 1f
                    energy = 1f
                }
                with<Fruit> {
                    age = 0
                }
                with<Position> {}
                with<Genetic> {}
                with<Child> {
                    parent = engine.entity {
                        with<Parent>{}
                        with<Genetic>{}
                    }
                }
            }
            val maxAge = defaultDna.fruits.bloomingDuration +
                    defaultDna.fruits.growingDuration +
                    defaultDna.fruits.fruitDuration
            for (i in 0 .. maxAge + 1) {
                fruitSystem!!.tick(1)
            }
            expect(fruits.has(fruit)).equal(false)
        }
    }
})

