package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import ktx.log.*

class Leaf(
    var rotation: Float = 0f,
    var generation: Int = 0,
    var positionAlongBranch: Float = 1f,
    var parent: Entity? = null
) : SerializableComponent() {
    constructor(obj: JsonObject, engine: Engine) : this(
        obj["rotation"].float,
        obj["generation"].int,
        obj["positionAlongBranch"].float,
        engine.entities.find{ uuids.get(it).id == obj["parent"].nullString }
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Leaf",
            "rotation" to rotation,
            "generation" to generation,
            "positionAlongBranch" to positionAlongBranch,
            "parent" to if (parent != null) uuids.get(parent).id else null
        )
    }
}
