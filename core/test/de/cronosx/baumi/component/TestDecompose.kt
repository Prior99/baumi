package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestDecompose : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Decompose component") {
        it("(de-)serializes correctly") {
            val component = Decompose(
                    current = 10f,
                    max = 100f,
                    speed = 1f
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Decompose",
                    "current" to 10f,
                    "max" to 100f,
                    "speed" to 1f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Decompose
            expect(deserialized is Decompose).equal(true)
            expect(deserialized.current).equal(10f)
            expect(deserialized.max).equal(100f)
            expect(deserialized.speed).equal(1f)
        }
    }
})