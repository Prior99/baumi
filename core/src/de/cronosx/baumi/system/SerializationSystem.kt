package de.cronosx.baumi.system

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

val version = 1

class SerializationSystem() : IntervalSystem(config.serializationInterval) {
    val uuids = mapperFor<Uuid>()

    override fun updateInterval() {
        for (entity in engine.entities) {
            if (!uuids.has(entity)) {
                entity.add(Uuid())
            }
        }
        val entities = engine.entities.map{ entity ->
            jsonObject(
                "components" to jsonArray(entity.getComponents()
                    .filter{
                        if (!(it is SerializableComponent)) {
                            error { "Component $it is not serializable." }
                        }
                        it is SerializableComponent
                    }
                    .map{ component ->
                        (component as SerializableComponent).toJson()
                    }
                )
            )
        }
        val obj = jsonObject(
            "entities" to entities,
            "world" to world.toJson(),
            "version" to version,
            "timestamp" to System.currentTimeMillis()
        )
    }
}
