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
    val parents = mapperFor<Parent>()
    val children = mapperFor<Child>()

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
                with<Parent> {}
                with<Branch> {}
            }
            val entity = engine.entity{
                with<Leaf> {}
                with<Child> {
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
            parents.get(parentEntity).children.add(entity)
            leafSystem!!.tick(1)
            expect(movables.get(entity).fixed).equal(false)
            expect(children.get(entity).parent).equal(null)
            expect(parents.get(parentEntity).children).not.contain(entity)
        }

        it("ignores leafs which are not decomposed enough") {
            val parentEntity = engine.entity {
                with<Health> {
                    current = 1f
                    max = 1f
                }
                with<Branch> {}
                with<Parent> {}
            }
            val entity = engine.entity{
                with<Leaf> {}
                with<Child> {
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
            parents.get(parentEntity).children.add(entity)
            leafSystem!!.tick(1)
            expect(movables.get(entity).fixed).equal(true)
            expect(children.get(entity).parent).equal(parentEntity)
            expect(parents.get(parentEntity).children).contain(entity)
        }
    }
})

