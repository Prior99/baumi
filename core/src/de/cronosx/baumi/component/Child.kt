package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Child(
        var generation: Int = 0,
        var positionAlongParent: Float = 1f,
        var parent: Entity? = null
) : SerializableComponent() {
    constructor(obj: JsonObject, engine: Engine) : this(
            obj["generation"].int,
            obj["positionAlongParent"].float,
            engine.entities.find { uuids.get(it).id == obj["parent"].nullString }
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "Child",
                "generation" to generation,
                "positionAlongParent" to positionAlongParent,
                "parent" to if (parent != null) uuids.get(parent).id else null
        )
    }
}

