package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestBuffer : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Buffer component") {
        it("(de-)serializes correctly") {
            val component = Buffer(
                    max = 10f,
                    current = 5f,
                    energyYield = 1f
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Buffer",
                    "max" to 10f,
                    "current" to 5f,
                    "energyYield" to 1f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Buffer
            expect(deserialized is Buffer).equal(true)
            expect(deserialized.max).equal(component.max)
            expect(deserialized.current).equal(component.current)
            expect(deserialized.energyYield).equal(component.energyYield)
        }
    }
})