package de.cronosx.baumi.component

import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class GroundWater() : SerializableComponent() {
    constructor(obj: JsonObject) : this()

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "GroundWater"
        )
    }
}
