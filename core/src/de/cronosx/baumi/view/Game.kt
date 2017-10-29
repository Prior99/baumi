package de.cronosx.baumi.view

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.math.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.math.*
import ktx.actors.*
import de.cronosx.baumi.system.*
import de.cronosx.baumi.system.tick.Ticker
import de.cronosx.baumi.data.*
import com.badlogic.gdx.input.GestureDetector.*
import com.badlogic.gdx.input.*
import com.badlogic.gdx.Input.Keys
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import de.cronosx.baumi.appWidth
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.Version
import de.cronosx.baumi.system.Renderer
import ktx.ashley.entity
import ktx.log.info
import de.cronosx.baumi.Application

class Game (val stage: Stage, val batch: Batch, val application: Application) : KtxScreen {
    var engine = PooledEngine()
    // Input.
    val gestureListener = GameGestureListener()
    val input = GameInputAdapter()
    // Create all the systems
    val serializationSystem = SerializationSystem()
    val shapeRenderer = ShapeRenderer()
    val ticker = Ticker()
    val rain = Rain()
    val dragging = Dragging(ticker)
    val gravity = Gravity()
    val wind = Wind()
    val clouds = Clouds()
    val cartSystem = CartSystem()
    val renderer = Renderer(batch, ticker)
    val fertilizer = Fertilizer()
    val debugRenderer = DebugRenderer(shapeRenderer)

    fun projectCoords(x: Int, y: Int): Vector2 {
        val vector3d = stage.camera.unproject(vec3(x.toFloat(), y.toFloat(), 0f))
        return vec2(vector3d.x, vector3d.y)
    }

    inner class GameInputAdapter : InputAdapter() {
        override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
            // The y axis is inverted for touching.
            dragging.touchDown(projectCoords(x, y))
            return false
        }

        override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
            dragging.touchUp()
            return false
        }

        override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
            dragging.touchDragged(projectCoords(x, y))
            return false
        }

        override fun keyDown(keycode: Int): Boolean {
            if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
                serializationSystem.save()
                application.setScreen<TreesMenu>()
            }
            return false
        }
    }

    inner class GameGestureListener : GestureListener {
        override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean { return false }
        override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean { return false }
        override fun longPress(x: Float, y: Float): Boolean { return false }
        override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean { return false }
        override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean { return false }
        override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean { return false }
        override fun zoom (originalDistanc: Float, crrentDistance: Float): Boolean { return false; }
        override fun pinch(
                initialFirstPointer: Vector2,
                initialSecondPointer: Vector2,
                firstPointer: Vector2,
                secondPointer: Vector2): Boolean {
            return false
        }
        override fun pinchStop() {}
    }

    val view = table {
        setFillParent(true)
    }

    override fun show() {
        engine = PooledEngine()
        engine.addSystem(ticker)
        engine.addSystem(gravity)
        engine.addSystem(wind)
        engine.addSystem(clouds)
        engine.addSystem(renderer)
        engine.addSystem(debugRenderer)
        engine.addSystem(serializationSystem)
        engine.addSystem(rain)
        engine.addSystem(cartSystem)
        engine.addSystem(fertilizer)
        engine.addSystem(dragging)
        stage.addActor(view)
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(GestureDetector(gestureListener))
        multiplexer.addProcessor(input)
        Gdx.input.inputProcessor = multiplexer
        view.setKeyboardFocus()
        view.onKey { key ->
            when (key) {
                'r' -> debug.disableRendering = !debug.disableRendering
                'd' -> debug.enableDebugRendering = !debug.enableDebugRendering
                'b' -> debug.infiniteBuffers = !debug.infiniteBuffers
                's' -> debug.extremeSpeed = !debug.extremeSpeed
            }
        }
    }

    override fun render(delta: Float) {
        stage.act(delta)
        shapeRenderer.setProjectionMatrix(stage.camera.combined)
        engine.update(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

    fun newGame() {
        // Create the root tree branch.
        engine.entity {
            with<Position> {
                position = vec2(appWidth/ 2f, 360f)
            }
            with<Branch> {
                rotation = defaultDna.rotation.initial
                length = defaultDna.length.initial
                maxLength = defaultDna.length.max
            }
            with<Parent> {
                children = ArrayList()
            }
            with<Child> {
                generation = 0
                parent = null
                positionAlongParent = 0f
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

    fun load(obj: JsonObject): Boolean {
        val saveGameVersion = obj["version"].nullString?.let { Version(it) } ?: Version(0, 0, 0)
        info { "Savegame version: ${saveGameVersion}" }
        info { "Software version: ${config.version}" }
        if (!saveGameVersion.isCompatible(config.version)) {
            error { "Migrating of savegames not yet implemented. Starting new game." }
            newGame()
            return false
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
        return true
    }
}
