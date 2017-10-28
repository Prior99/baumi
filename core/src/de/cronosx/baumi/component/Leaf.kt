package de.cronosx.baumi.component

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import ktx.log.*

class Leaf(
    var rotation: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["rotation"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Leaf",
            "rotation" to rotation
        )
    }
}
