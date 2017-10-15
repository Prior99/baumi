package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Producer(
    var rate: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["rate"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Producer",
            "rate" to rate
        )
    }
}
