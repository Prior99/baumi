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

class TestBranch : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Branch component") {
        it("(de-)serializes correctly") {
            val child = engine!!.entity{
                with<Uuid> { id = "some-id" }
            }
            val component = Branch(
                    rotation = 3.5f,
                    length = 8f,
                    maxLength = 100f,
                    generation = 3,
                    children = mutableListOf(child)
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Branch",
                    "rotation" to 3.5f,
                    "length" to 8f,
                    "maxLength" to 100f,
                    "generation" to 3,
                    "children" to jsonArray("some-id")
            ))
            val deserialized = deserializeComponent(json, engine!!) as Branch
            expect(deserialized is Branch).equal(true)
            expect(deserialized.rotation).equal(3.5f)
            expect(deserialized.length).equal(8f)
            expect(deserialized.maxLength).equal(100f)
            expect(deserialized.generation).equal(3)
            expect(deserialized.children).contain(child)
        }
    }
})