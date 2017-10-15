package de.cronosx.baumi.component

import ktx.ashley.*
import de.cronosx.baumi.data.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Genetic(
    var dna: DNA = defaultDna
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        defaultDna
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Genetic",
            "dna" to "TODO"
        )
    }
}
