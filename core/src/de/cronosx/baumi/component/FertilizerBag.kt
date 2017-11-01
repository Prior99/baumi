package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class FertilizerBag(
        var content: Int = 0
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(obj["content"].int)

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "FertilizerBag",
                "content" to content
        )
    }
}
