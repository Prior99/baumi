package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class RainDrop(
        var index: Int = 0
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
            obj["index"].int
    )

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "RainDrop",
                "index" to index
        )
    }
}
