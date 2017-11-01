package de.cronosx.baumi.data

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import java.util.*

class World (
    var tick: Int,
    var lastTick: Double,
    var windDirection: Float,
    var id: String,
    var name: String
) {
    constructor(obj: JsonObject) : this(
        obj["tick"].int,
        obj["lastTick"].double,
        obj["windDirection"].float,
        obj["id"].string,
        obj["name"].string
    )

    fun toJson(): JsonObject {
        return jsonObject(
            "tick" to tick,
            "lastTick" to lastTick,
            "windDirection" to windDirection,
            "id" to id,
            "name" to name
        )
    }
}

fun createDefaultWorld(): World {
    return World(
        tick = 0,
        lastTick = System.currentTimeMillis().toDouble() / 1000.0,
        windDirection = Math.random().toFloat() * 20f - 10f,
        id = UUID.randomUUID().toString(),
        name = "Default tree"
    )
}

var world = createDefaultWorld()