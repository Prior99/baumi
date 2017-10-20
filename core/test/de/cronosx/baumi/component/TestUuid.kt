package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.github.salomonbrys.kotson.jsonObject
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestUuid : Spek({
    var engine: Engine? = null

    beforeEachTest {
        engine = PooledEngine()
    }

    describe("The Uuid component") {
        it("(de-)serializes correctly") {
            val component = Uuid(
                    id = "some-id"
            )
            val json = component.toJson()
            expect(json).equal(jsonObject(
                    "type" to "Uuid",
                    "id" to "some-id"
            ))
            val deserialized = deserializeComponent(json, engine!!) as Uuid
            expect(deserialized is Uuid).equal(true)
            expect(deserialized.id).equal("some-id")
        }
    }
})