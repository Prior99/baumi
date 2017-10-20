package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import ktx.ashley.entity
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestGroundWater : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The GroundWater component") {
        it("(de-)serializes correctly") {
            val parentEntity = engine!!.entity{
                with<Uuid> { id = "some-id" }
            }
            val component = GroundWater()
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "GroundWater"
            ))
            val deserialized = deserializeComponent(json, engine!!) as GroundWater
            expect(deserialized is GroundWater).equal(true)
        }
    }
})