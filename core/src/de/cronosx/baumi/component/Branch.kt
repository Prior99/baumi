package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Branch(
    var rotation: Float = 0f,
    var length: Float = 0f,
    var maxLength: Float = 0f,
    var generation: Int = 0,
    var children: MutableList<Entity> = ArrayList()
) : SerializableComponent() {
    constructor(obj: JsonObject, engine: Engine) : this(
        obj["rotation"].float,
        obj["length"].float,
        obj["maxLength"].float,
        obj["generation"].int,
        obj["children"].array.map{ child ->
            engine.entities.find{ uuids.get(it).id == child["id"].string } as Entity
        }.toMutableList()
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Branch",
            "rotation" to rotation,
            "length" to length,
            "maxLength" to maxLength,
            "generation" to generation,
            "children" to jsonArray(children.map{ uuids.get(it).id })
        )
    }
}
