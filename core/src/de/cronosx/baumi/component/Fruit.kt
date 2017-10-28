package de.cronosx.baumi.component

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Fruit(
    var rotation: Float = 0f,
    var age: Int = 0
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["rotation"].float,
        obj["age"].int
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Fruit",
            "rotation" to rotation,
            "age" to age
        )
    }
}
