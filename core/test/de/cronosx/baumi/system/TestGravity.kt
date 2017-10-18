package de.cronosx.baumi.system

import com.badlogic.ashley.core.PooledEngine
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.Movable
import de.cronosx.baumi.component.Position
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestGravity : Spek({
    var engine = PooledEngine()

    val positions = mapperFor<Position>()

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Gravity system") {
        it("moves entities with `Movable` down") {
            val entity = engine.entity {
                with<Position>{ position = vec2(800f, 800f)}
                with<Movable>{
                    weight = 100f
                    fixed = false
                    floating = false
                }
            }
            engine.update(4f)
            expect(positions.get(entity).position.y).to.be.equal(400f)
        }
    }
})