package de.cronosx.baumi.component

import ktx.ashley.*
import de.cronosx.baumi.data.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Health(
    var max: Float = 0f,
    var current: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["max"].float,
        obj["current"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Health",
            "max" to max,
            "current" to current
        )
    }

    val alive: Boolean
        get() = current > 0f

    fun kill() {
        current = 0f
    }
}
