package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import ktx.ashley.entity
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestMovable : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Movable component") {
        it("(de-)serializes correctly") {
            val component = Movable(
                    weight = 3.5f,
                    floating = true,
                    fixed = true
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Movable",
                    "weight" to 3.5f,
                    "floating" to true,
                    "fixed" to true
            ))
            val deserialized = deserializeComponent(json, engine!!) as Movable
            expect(deserialized is Movable).equal(true)
            expect(deserialized.weight).equal(3.5f)
            expect(deserialized.floating).equal(true)
            expect(deserialized.fixed).equal(true)
        }
    }
})