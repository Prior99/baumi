package de.cronosx.baumi.system

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.Math.FloatMath
import de.cronosx.baumi.component.Movable
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.data.world
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestWind : Spek({
    var engine = PooledEngine()

    val epsilon = 5
    val positions = mapperFor<Position>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Wind system") {
        beforeEachTest {
            engine.addSystem(Wind())
        }

        it("moves entities with `Movable` according to the world's wind direction and their weight") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 800f)}
                with<Movable>{
                    weight = 100f
                    fixed = false
                    floating = false
                }
            }
            engine.update(4f)
            val expected = 800f + 4f * world.windDirection
            expect(positions.get(entity).position.x).satisfy{ Math.abs(expected - it) < epsilon }
        }

        it("doesn't move entities with `Movable` and `fixed = true`") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 800f)}
                with<Movable>{
                    weight = 100f
                    fixed = true
                    floating = false
                }
            }
            engine.update(4f)
            expect(positions.get(entity).position.x).equal(800f)
        }

        it("moves entities with `Movable` and `floating = true`") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 800f)}
                with<Movable>{
                    weight = 100f
                    fixed = false
                    floating = true
                }
            }
            engine.update(4f)
            val expected = 800f + 4f * world.windDirection
            expect(positions.get(entity).position.x).satisfy{ Math.abs(expected - it) < epsilon }
        }
    }
})

