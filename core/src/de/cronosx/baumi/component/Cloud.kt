package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Cloud(
    var index: Int = 0
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["index"].int
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Cloud",
            "index" to index
        )
    }
}
