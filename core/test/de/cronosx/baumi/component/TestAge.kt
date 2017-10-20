package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestAge : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Age component") {
        it("(de-)serializes correctly") {
            val component = Age(age = 7)
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Age",
                    "age" to 7
            ))
            val deserialized = deserializeComponent(json, engine!!) as Age
            expect(deserialized is Age).equal(true)
            expect(deserialized.age).equal(component.age)
        }
    }
})

