package de.cronosx.baumi.component

import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Decompose(
    var current: Float = 0f,
    var max: Float = 0f,
    var speed: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["current"].float,
        obj["max"].float,
        obj["speed"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Decompose",
            "current" to current,
            "max" to max,
            "speed" to speed
        )
    }
}
