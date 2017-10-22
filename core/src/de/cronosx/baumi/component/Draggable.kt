package de.cronosx.baumi.component

import com.badlogic.gdx.math.Vector2
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import ktx.math.vec2

class Draggable(
        var offset: Vector2 = vec2(0f, 0f),
        var size: Vector2 = vec2(0f, 0f)
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
            vec2(obj["offset"].array[0].float, obj["offset"].array[1].float),
            vec2(obj["size"].array[0].float, obj["size"].array[1].float)
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "Draggable",
                "offset" to jsonArray(offset.x, offset.y),
                "size" to jsonArray(size.x, size.y)
        )
    }
}

