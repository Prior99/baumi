package de.cronosx.baumi.system

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonParser
import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import de.cronosx.baumi.data.*
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.events.Drag
import ktx.ashley.*
import ktx.math.*
import ktx.log.*

class SerializationSystem() : IntervalSystem(config.serializationInterval) {
    val uuids = mapperFor<Uuid>()

    fun newGame(engine: Engine) {
        // Create the root tree branch.
        engine.entity {
            with<Position> {
                position = vec2(appWidth/ 2f, 360f)
            }
            with<Branch> {
                rotation = defaultDna.rotation.initial
                length = defaultDna.length.initial
                maxLength = defaultDna.length.max
                children = ArrayList()
            }
            with<Genetic> {
                dna = defaultDna
            }
            with<Health> {
                max = defaultDna.health.max
                current = defaultDna.health.max
            }
            with<Consumer> {
                maxEnergy = defaultDna.energy.max
                minEnergy = maxEnergy / 2f
                energy = minEnergy
                rate = defaultDna.energy.upkeep
            }
            with<Root> {}
            with<Uuid> {}
        }
        // Ground water.
        engine.entity {
            with<Producer> {
                rate = 0f
            }
            with<Buffer> {
                max = config.maxWater
                current = config.initialWater
                energyYield = config.waterEnergyYield
            }
            with<GroundWater> {}
            with<Uuid> {}
        }
        // Cart.
        engine.entity {
            with<Position> {
                position = vec2(20f, 360f)
            }
            with<Draggable> {
                size = vec2(300f, 120f)
            }
            with<Cart> {
                content = 0
                angle = 0f
            }
        }
    }

    override fun addedToEngine(engine: Engine) {
        val files = Gdx.files.local("trees/").list()
        if (files.count() == 0) {
            info { "No old game found. Starting new game." }
            newGame(engine)
            return
        }
        val parser = JsonParser()
        val obj = parser.parse(files[0].readString())
        val saveGameVersion = obj["version"].nullString?.let { Version(it) } ?: Version(0, 0, 0)
        info { "Savegame version: ${saveGameVersion}" }
        info { "Software version: ${config.version}" }
        if (!saveGameVersion.isCompatible(config.version)) {
            error { "Migrating of savegames not yet implemented. Starting new game." }
            files[0].delete()
            newGame(engine)
            return
        }
        world = World(obj["world"].obj)
        info { "Loaded game \"${world.id}\" with name \"${world.name}\"." }
        info { "Game is at tick ${world.tick}" }
        // Uuids have to be deserialized first.
        for (entityObj in obj["entities"].array) {
            val entity = engine.entity {
                with<Uuid> {
                    id = entityObj["id"].string
                }
            }
        }
        for (entityObj in obj["entities"].array) {
            val id = entityObj["id"].string
            val entity = engine.entities.find { uuids.get(it).id == id }
            if (entity == null) {
                error { "Unable to find entity with id \"$id\" which was just created." }
                continue
            }
            entityObj["components"].array
                .map { deserializeComponent(it.obj, engine) }
                .filter {
                    if (it == null) {
                        error { "Encountered undeserializable component." }
                    }
                    it != null
                }
                .forEach { entity?.add(it) }
        }
        info { "Loaded ${engine.entities.count()} entities." }
    }

    fun save() {
        for (entity in engine.entities) {
            if (!uuids.has(entity)) {
                entity.add(Uuid())
            }
        }
        val entities = jsonArray(engine.entities.map { entity ->
            jsonObject(
                "components" to jsonArray(entity.getComponents()
                    .filter {
                        if (!(it is SerializableComponent)) {
                            error { "Component $it is not serializable." }
                        }
                        it is SerializableComponent &&
                        // The id's will be stored seperately.
                        !(it is Uuid)
                    }
                    .map { component ->
                        (component as SerializableComponent).toJson()
                    }
                ),
                "id" to uuids.get(entity).id
            )
        })
        val obj = jsonObject(
            "entities" to entities,
            "world" to world.toJson(),
            "version" to config.version.toString(),
            "timestamp" to System.currentTimeMillis()
        )
        val json = obj.toString()
        val path = "trees/${world.id}"
        val file = Gdx.files.local(path)
        file.writeString(json, false)
        info { "Wrote ${json.length} characters to $path." }
    }

    override fun updateInterval() {
        save()
    }
}
