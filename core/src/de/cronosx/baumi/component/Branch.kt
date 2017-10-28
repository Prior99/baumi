package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Branch(
    var rotation: Float = 0f,
    var length: Float = 0f,
    var maxLength: Float = 0f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["rotation"].float,
        obj["length"].float,
        obj["maxLength"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Branch",
            "rotation" to rotation,
            "length" to length,
            "maxLength" to maxLength
        )
    }
}
