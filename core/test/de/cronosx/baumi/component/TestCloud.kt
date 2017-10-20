package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestCloud : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Cloud component") {
        it("(de-)serializes correctly") {
            val component = Cloud(content = 100f)
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Cloud",
                    "content" to 100f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Cloud
            expect(deserialized is Cloud).equal(true)
            expect(deserialized.content).equal(100f)
        }
    }
})