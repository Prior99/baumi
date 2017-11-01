package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Entity
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

    val branches = mapperFor<Branch>()
    val parents = mapperFor<Parent>()
    val consumers = mapperFor<Consumer>()
    val leafs = mapperFor<Leaf>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Growth system") {
        var growth: Growth? = null
        var root: Entity? = null

        beforeEachTest {
            growth = Growth(engine)
            root = engine.entity {
                with<Position> {
                    position = vec2(540f, 360f)
                }
                with<Branch> {
                    rotation = 0f
                    length = 0f
                    maxLength = 100f
                }
                with<Parent> {}
                with<Child> {}
                with<Genetic> {}
                with<Health> {
                    max = 100f
                    current = 100f
                }
                with<Consumer> {
                    maxEnergy = 100f
                    minEnergy = 50f
                    energy = 100f
                    rate = 1f
                }
                with<Root> {}
            }
        }

        describe("creating leafs") {
            it("creates leafs if enough surplus is available") {
                branches.get(root).length = 100f
                val ticks = (defaultDna.leafs.maxGenerationLeafCountPerLength * 100f).toInt()
                // Grows leafs until max.
                for (i in 1 .. ticks) {
                    consumers.get(root).energy = 100f + defaultDna.leafs.leafCost
                    growth!!.tick(i)
                    // Only one leaf can be growing at a time.
                    if (i == 1) {
                        expect(consumers.get(root).energy).equal(92f)
                    }
                    expect(engine.entities.filter { leafs.has(it) }.count()).equal(minOf(i, defaultDna.leafs.maxYoungLeafs))
                }
                // Doesn't grow more leafs than maximum allowed.
                consumers.get(root).energy = 50f + defaultDna.leafs.leafCost
                growth!!.tick(ticks + 1)
                expect(consumers.get(root).energy).equal(50f + defaultDna.leafs.leafCost)
                expect(engine.entities.filter{ leafs.has(it) }.count()).equal(defaultDna.leafs.maxYoungLeafs)
            }

            it("creates no leafs if no surplus is available") {
                branches.get(root).length = 100f
                val ticks = (defaultDna.leafs.maxGenerationLeafCountPerLength * 100f).toInt()
                for (i in 1 .. ticks) {
                    consumers.get(root).energy = 50f
                    growth!!.tick(i)
                    expect(consumers.get(root).energy).equal(50f)
                    expect(engine.entities.filter{ leafs.has(it) }.count()).equal(0)
                }
            }
        }

        describe("creating branches") {
            it("creates branches if enough surplus is available to support branches and leafs") {
                branches.get(root).length = 100f
                consumers.get(root).energy = 50f + defaultDna.branching.branchCost + defaultDna.leafs.leafCost
                growth!!.tick(1)
                expect(engine.entities.filter{ leafs.has(it) }.count()).equal(1)
                expect(engine.entities.filter{ branches.has(it) }.count()).above(1)
            }

            it("creates branches if enough surplus is available and there are too many leafs already") {
                val parentBranch = parents.get(root)
                for (i in 1 .. 100) {
                    parentBranch.children.add(engine.entity{
                        with<Leaf> {}
                        with<Child> {
                            parent = root
                        }
                        with<Position> {}
                    })
                }
                branches.get(root).length = 100f
                consumers.get(root).energy = 50f + defaultDna.branching.branchCost
                growth!!.tick(1)
                expect(engine.entities.filter{ leafs.has(it) }.count()).equal(100)
                expect(engine.entities.filter{ branches.has(it) }.count()).above(1)
            }
        }
        describe("growing of length") {
            it("increases the length if enough surplus is available and there are already enough leafs") {
                val parentBranch = parents.get(root)
                for (i in 1 .. 100) {
                    parentBranch.children.add(engine.entity{
                        with<Leaf> {}
                        with<Child> {
                            parent = root
                        }
                        with<Position> {}
                    })
                }
                branches.get(root).length = 0f
                consumers.get(root).energy = 50f + defaultDna.length.growCost
                growth!!.tick(1)
                expect(branches.get(root).length).equal(defaultDna.length.growthSpeed)
            }
        }
    }
})
