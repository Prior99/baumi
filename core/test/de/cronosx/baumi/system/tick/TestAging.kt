package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestAging : Spek({
    var engine = PooledEngine()

    val ages = mapperFor<Age>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Aging system") {
        var aging: Aging? = null

        beforeEachTest {
            aging = Aging(engine)
        }

        it("increases the age of an entity with an `Age` component") {
            val entity = engine.entity{
                with<Age>{}
            }
            aging!!.tick(1)
            expect(ages.get(entity).age).equal(1)
            aging!!.tick(2)
            expect(ages.get(entity).age).equal(2)
        }
    }
})

