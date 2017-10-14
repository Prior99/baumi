package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import de.cronosx.baumi.data.*
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject

class Consumer(
    var maxEnergy: Float = 0f,
    var minEnergy: Float = 0f,
    var energy: Float = 0f,
    var rate: Float = 0f,
    var effectiveness: Float = 1f,
    var healthDecayRate: Float = 0.001f
) : SerializableComponent() {
    constructor(obj: JsonObject) : this(
        obj["maxEnergy"].float,
        obj["minEnergy"].float,
        obj["energy"].float,
        obj["rate"].float,
        obj["effectiveness"].float,
        obj["healthDecayRate"].float
    ) {}

    override fun toJson(): JsonObject {
        return jsonObject(
            "type" to "Consumer",
            "maxEnergy" to maxEnergy,
            "minEnergy" to minEnergy,
            "energy" to energy,
            "rate" to rate,
            "effectiveness" to effectiveness,
            "healthDecayRate" to healthDecayRate
        )
    }

    val remainingBufferCapacity: Float
        get() = maxEnergy - energy
}
