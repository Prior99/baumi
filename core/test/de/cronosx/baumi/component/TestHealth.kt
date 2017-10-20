package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestHealth : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Health component") {
        it("(de-)serializes correctly") {
            val component = Health(
                    max = 100f,
                    current = 4f
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Health",
                    "max" to 100f,
                    "current" to 4f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Health
            expect(deserialized is Health).equal(true)
            expect(deserialized.max).equal(100f)
            expect(deserialized.current).equal(4f)
        }
    }
})