package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestRainDrop : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The RainDrop component") {
        it("(de-)serializes correctly") {
            val component = RainDrop(
                    index = 3
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "RainDrop",
                    "index" to 3
            ))
            val deserialized = deserializeComponent(json, engine!!) as RainDrop
            expect(deserialized is RainDrop).equal(true)
            expect(deserialized.index).equal(3)
        }
    }
})