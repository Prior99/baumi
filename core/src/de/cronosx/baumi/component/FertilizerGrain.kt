package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class FertilizerGrain(
        var index: Int = 0,
        var rotation: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
            obj["index"].int,
            obj["rotation"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "FertilizerGrain",
                "index" to index,
                "rotation" to rotation
        )
    }
}

