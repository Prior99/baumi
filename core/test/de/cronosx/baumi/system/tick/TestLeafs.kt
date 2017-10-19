package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestLeafs : Spek({
    var engine = PooledEngine()

    val movables = mapperFor<Movable>()
    val branches = mapperFor<Branch>()
    val leafs = mapperFor<Leaf>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Leaf system") {
        var leafSystem: Leafs? = null

        beforeEachTest {
            leafSystem = Leafs(engine)
        }

        it("detaches a far enough decomposed leaf from it's parent") {
            val parentEntity = engine.entity {
                with<Health> {
                    current = 1f
                    max = 1f
                }
                with<Branch> { }
            }
            val entity = engine.entity{
                with<Leaf>{
                    parent = parentEntity
                }
                with<Movable> {
                    weight = 100f
                    fixed = true
                }
                with<Decompose>{
                    current = 4f
                }
            }
            branches.get(parentEntity).children.add(entity)
            leafSystem!!.tick(1)
            expect(movables.get(entity).fixed).equal(false)
            expect(leafs.get(entity).parent).equal(null)
            expect(branches.get(parentEntity).children).not.contain(entity)
        }

        it("ignores leafs which are not decomposed enough") {
            val parentEntity = engine.entity {
                with<Health> {
                    current = 1f
                    max = 1f
                }
                with<Branch> { }
            }
            val entity = engine.entity{
                with<Leaf>{
                    parent = parentEntity
                }
                with<Movable> {
                    weight = 100f
                    fixed = true
                }
                with<Decompose>{
                    current = 0f
                }
            }
            branches.get(parentEntity).children.add(entity)
            leafSystem!!.tick(1)
            expect(movables.get(entity).fixed).equal(true)
            expect(leafs.get(entity).parent).equal(parentEntity)
            expect(branches.get(parentEntity).children).contain(entity)
        }
    }
})

