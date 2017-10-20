package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import ktx.math.vec2
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestPosition : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Position component") {
        it("(de-)serializes correctly") {
            val component = Position(position = vec2(400f, 380f))
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Position",
                    "position" to jsonArray(400f, 380f)
            ))
            val deserialized = deserializeComponent(json, engine!!) as Position
            expect(deserialized is Position).equal(true)
            expect(deserialized.position).equal(vec2(400f, 380f))
        }
    }
})