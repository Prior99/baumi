package de.cronosx.baumi.system

import com.github.salomonbrys.kotson.*
import de.cronosx.baumi.component.*
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import de.cronosx.baumi.data.*
import ktx.ashley.*
import ktx.async.ktxAsync
import ktx.log.*

class SerializationSystem : IntervalSystem(config.serializationInterval) {
    val uuids = mapperFor<Uuid>()

    fun save(saveSynchroneous: Boolean = false) {
        for (entity in engine.entities) {
            if (!uuids.has(entity)) {
                entity.add(Uuid())
            }
        }
        val entities = jsonArray(engine.entities.map { entity ->
            jsonObject(
                "components" to jsonArray(entity.components
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
        // Create directory.
        val directory = "trees/${world.id}"
        Gdx.files.local(directory).mkdirs()
        // Write json.
        val json = obj.toString()
        val path = "${directory}/game.json"
        val file = Gdx.files.local(path)
        file.writeString(json, false)
        info { "Wrote ${json.length} characters to $path." }
        // Create screenshot.
        val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)
        fun saveScreenshot() {
            val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
            BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
            PixmapIO.writePNG(Gdx.files.local("${directory}/screenshot.png"), pixmap)
            pixmap.dispose()
            info { "Screenshot saved." }
        }
        if (saveSynchroneous) {
            saveScreenshot()
        } else {
            ktxAsync {
                asynchronous {
                    saveScreenshot()
                }
            }
        }
    }

    override fun updateInterval() {
        save()
    }
}
