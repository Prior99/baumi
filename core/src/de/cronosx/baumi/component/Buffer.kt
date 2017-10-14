package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Buffer(
    var max: Float = 0f,
    var current: Float = 0f,
    var energyYield: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["max"].float,
        obj["current"].float,
        obj["energyYield"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Buffer",
            "max" to max,
            "current" to current,
            "energyYield" to energyYield
        )
    }
}
