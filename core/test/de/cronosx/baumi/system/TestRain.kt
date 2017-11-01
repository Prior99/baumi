package de.cronosx.baumi.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.Bus
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.config
import de.cronosx.baumi.system.tick.Ticker
import ktx.ashley.add
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestRain : Spek({
    var engine = PooledEngine()

    val positions = mapperFor<Position>()
    val rainDrops = mapperFor<RainDrop>()
    val buffers = mapperFor<Buffer>()
    val movables = mapperFor<Movable>()

    var rain: Rain? = null
    var dragging: Dragging? = null

    beforeEachTest {
        engine = PooledEngine()
        Bus.reset()
    }

    describe("The Rain system") {
        var buffer: Entity? = null

        beforeEachTest {
            rain = Rain()
            dragging = Dragging(Ticker())
            engine.addSystem(rain)
            engine.addSystem(dragging)
            buffer = engine.entity {
                with<Buffer>{
                    max = 100f
                    current = 0f
                }
                with<GroundWater>{}
            }
        }

        it("handles entities with `RainDrop` which are about to hit the ground") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 100f)}
                with<RainDrop>{}
            }
            engine.update(4f)
            expect(rainDrops.has(entity)).equal(false)
            expect(positions.has(entity)).equal(false)
            expect(buffers.get(buffer).current).equal(config.dropContent)
        }

        it("ignores entities with `RainDrops` which did not hit the ground") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 800f)}
                with<RainDrop>{}
            }
            engine.update(4f)
            expect(rainDrops.has(entity)).equal(true)
            expect(positions.has(entity)).equal(true)
            expect(buffers.get(buffer).current).equal(0f)
        }
        
        describe("with a cloud") {
            var cloud: Entity? = null
            
            beforeEachTest { 
                cloud = engine.entity { 
                    with<Position>{ position = vec2(800f, 800f) }
                    with<Cloud>{
                        content = 100f
                    }
                    with<Movable>{
                        weight = 100f
                        fixed = false
                        floating = true
                    }
                    with<Draggable>{
                        size = vec2(500f, 250f)
                    }
                }
            }
            
            describe("with the user having touched the cloud") {
                beforeEachTest {
                    dragging!!.touchDown(vec2(820f, 820f))
                }

                it("stores the touched cloud") {
                    expect(rain!!.current).equal(cloud)
                    expect(dragging!!.current!!.offsetToCursor).equal(vec2(20f, 20f))
                    expect(rain!!.timeContingent).equal(0f)
                }

                it("makes the cloud's `Movable` component to have `fixed = true`") {
                    expect(movables.get(cloud).fixed).equal(true)
                }

                describe("with the user having dragged the cloud") {
                    beforeEachTest {
                        dragging!!.touchDragged(vec2(840f, 820f))
                    }

                    it("moves the cloud") {
                        expect(positions.get(cloud).position).equal(vec2(820f, 800f))
                    }

                    it("spawns entities with `RainDrop` when 0.1s has passed") {
                        engine.update(1f)
                        expect(engine.entities.filter{ rainDrops.has(it) }.count()).equal(400)
                    }

                    it("spawns entities with `RainDrop` when 0.1s has passed with multiple updates") {
                        for (i in 1..15) {
                            engine.update(1f)
                        }
                        expect(engine.entities.filter{ rainDrops.has(it) }.count()).equal(400)
                    }
                }

                it("unsets the cloud with the user having lifted the finger") {
                    dragging!!.touchUp()
                    expect(rain!!.current).equal(null)
                    expect(dragging!!.current).equal(null)
                    expect(movables.get(cloud).fixed).equal(false)
                }
            }
        }
    }
})

