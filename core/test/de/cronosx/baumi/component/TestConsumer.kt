package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestConsumer : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Consumer component") {
        it("(de-)serializes correctly") {
            val component = Consumer(
                    maxEnergy = 100f,
                    minEnergy = 50f,
                    energy = 75f,
                    rate = 5f,
                    effectiveness = 0.5f,
                    healthDecayRate = 0.1f
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Consumer",
                    "maxEnergy" to 100f,
                    "minEnergy" to 50f,
                    "energy" to 75f,
                    "rate" to 5f,
                    "effectiveness" to 0.5f,
                    "healthDecayRate" to 0.1f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Consumer
            expect(deserialized is Consumer).equal(true)
            expect(deserialized.maxEnergy).equal(100f)
            expect(deserialized.minEnergy).equal(50f)
            expect(deserialized.energy).equal(75f)
            expect(deserialized.rate).equal(5f)
            expect(deserialized.effectiveness).equal(0.5f)
            expect(deserialized.healthDecayRate).equal(0.1f)
        }
    }
})