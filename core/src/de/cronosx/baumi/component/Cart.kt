package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Cart(
        var content: Int = 0,
        var angle: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
            obj["content"].int,
            obj["angle"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "Cart",
                "content" to content,
                "angle" to angle
        )
    }
}

