package de.cronosx.baumi.component

import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Movable(
    var weight: Float = 1f,
    var floating: Boolean = true,
    var fixed: Boolean = true
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["weight"].float,
        obj["floating"].bool,
        obj["fixed"].bool
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Movable",
            "weight" to weight,
            "floating" to floating,
            "fixed" to fixed
        )
    }
}
