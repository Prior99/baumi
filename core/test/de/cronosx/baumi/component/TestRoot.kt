package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestRoot : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Root component") {
        it("(de-)serializes correctly") {
            val component = Root()
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Root"
            ))
            val deserialized = deserializeComponent(json, engine!!) as Root
            expect(deserialized is Root).equal(true)
        }
    }
})