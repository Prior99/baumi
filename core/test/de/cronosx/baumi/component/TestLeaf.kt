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

class TestLeaf : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Leaf component") {
        it("(de-)serializes correctly") {
            val parentEntity = engine!!.entity{
                with<Uuid> { id = "some-id" }
            }
            val component = Leaf(
                    rotation = 3.5f
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Leaf",
                    "rotation" to 3.5f
            ))
            val deserialized = deserializeComponent(json, engine!!) as Leaf
            expect(deserialized is Leaf).equal(true)
            expect(deserialized.rotation).equal(3.5f)
        }
    }
})