package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestProducer : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Producer component") {
        it("(de-)serializes correctly") {
            val component = Producer(rate = 3.5f)
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Producer",
                    "rate" to 3.5f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Producer
            expect(deserialized is Producer).equal(true)
            expect(deserialized.rate).equal(3.5f)
        }
    }
})