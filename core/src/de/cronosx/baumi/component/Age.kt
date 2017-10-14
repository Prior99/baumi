package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.badlogic.ashley.core.Component
import ktx.ashley.*
import com.badlogic.ashley.core.Engine

class Age(
    var age: Int = 0
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(obj["age"].int) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Age",
            "age" to age
        )
    }
}
