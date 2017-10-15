package de.cronosx.baumi.component

import com.badlogic.gdx.math.Vector2
import ktx.math.*
import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Position(
    var position: Vector2 = vec2(0f, 0f)
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        vec2(obj["position"].array[0].float, obj["position"].array[1].float)
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Position",
            "position" to jsonArray(position.x, position.y)
        )
    }
}
