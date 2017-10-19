package de.cronosx.baumi.system

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.winterbe.expekt.expect
import de.cronosx.baumi.component.Cloud
import de.cronosx.baumi.component.Position
import de.cronosx.baumi.test.EmptyApplication
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestClouds : Spek({
    var engine = PooledEngine()

    val clouds = mapperFor<Cloud>()

    beforeEachTest {
        HeadlessApplication(EmptyApplication());
        engine = PooledEngine()
    }

    afterEachTest {
        Gdx.app.exit()
    }

    describe("The Clouds system") {
        beforeEachTest {
            engine.addSystem(Clouds())
        }

        it("removes cloud outside of the screen") {
            val entityRight = engine.entity {
                with<Position>{ position = vec2(2000f, 800f) }
                with<Cloud>{}
            }
            val entityLeft = engine.entity {
                with<Position>{ position = vec2(-1000f, 800f) }
                with<Cloud>{}
            }
            engine.update(1f)
            expect(clouds.has(entityRight)).equal(false)
            expect(clouds.has(entityLeft)).equal(false)
        }

        it("spawns a cloud if no clouds are present") {
            expect(engine.entities.filter{ clouds.has(it) }.count()).equal(0)
            engine.update(1f)
            expect(engine.entities.filter{ clouds.has(it) }.count()).equal(1)
        }
    }
})