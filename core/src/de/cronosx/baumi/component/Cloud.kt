package de.cronosx.baumi.component

import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Cloud(
    var content: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["content"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Cloud",
            "content" to content
        )
    }
}
