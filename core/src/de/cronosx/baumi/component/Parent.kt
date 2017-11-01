package de.cronosx.baumi.component

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Parent(
        var children: MutableList<Entity> = ArrayList()
) : SerializableComponent() {
    constructor(obj: JsonObject, engine: Engine) : this(
            obj["children"].array.map { child ->
                engine.entities.find { uuids.get(it).id == child.string } as Entity
            }.toMutableList()
    )

    override fun toJson(): JsonObject {
        return jsonObject(
                "type" to "Parent",
                "children" to jsonArray(children.map { uuids.get(it).id })
        )
    }
}

