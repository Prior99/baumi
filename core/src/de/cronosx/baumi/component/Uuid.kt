package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import java.util.UUID
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Uuid(
    var id: String = UUID.randomUUID().toString()
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["id"].string
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Uuid",
            "id" to id
        )
    }
}
