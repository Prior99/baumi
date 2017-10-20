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

class TestFruit : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Fruit component") {
        it("(de-)serializes correctly") {
            val parentEntity = engine!!.entity{
                with<Uuid> { id = "some-id" }
            }
            val component = Fruit(
                    rotation = 3.5f,
                    generation = 4,
                    positionAlongBranch = 3f,
                    parent = parentEntity,
                    age = 2
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Fruit",
                    "rotation" to 3.5f,
                    "generation" to 4,
                    "positionAlongBranch" to 3f,
                    "parent" to "some-id",
                    "age" to 2
            ))
            val deserialized = deserializeComponent(json, engine!!) as Fruit
            expect(deserialized is Fruit).equal(true)
            expect(deserialized.rotation).equal(3.5f)
            expect(deserialized.generation).equal(4)
            expect(deserialized.positionAlongBranch).equal(3f)
            expect(deserialized.parent).equal(parentEntity)
            expect(deserialized.age).equal(2)
        }
    }
})