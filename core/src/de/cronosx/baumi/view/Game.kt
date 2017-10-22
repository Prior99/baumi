package de.cronosx.baumi.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.math.*
import ktx.actors.*
import de.cronosx.baumi.system.*
import de.cronosx.baumi.system.renderer.*
import de.cronosx.baumi.system.tick.Ticker
import de.cronosx.baumi.data.*
import com.badlogic.gdx.input.GestureDetector.*
import com.badlogic.gdx.input.*
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val bus = RxBus()
    val engine = PooledEngine()
    // Input.
    val gestureListener = GameGestureListener()
    val input = GameInputAdapter()
    // Create all the systems
    val serializationSystem = SerializationSystem()
    val shapeRenderer = ShapeRenderer()
    val ticker = Ticker()
    val rain = Rain()
    val gravity = Gravity()
    val wind = Wind()
    val clouds = Clouds()
    val renderer = Renderer(batch, ticker)
    val debugRenderer = DebugRenderer(shapeRenderer)

    fun projectCoords(x: Int, y: Int): Vector2 {
        val vector3d = stage.camera.unproject(vec3(x.toFloat(), y.toFloat(), 0f))
        return vec2(vector3d.x, vector3d.y)
    }

    inner class GameInputAdapter : InputAdapter() {
        override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
            // The y axis is inverted for touching.
            rain.touchDown(projectCoords(x, y))
            return false
        }

        override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
            rain.touchUp()
            return false
        }

        override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
            rain.touchDragged(projectCoords(x, y))
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
        engine.addSystem(ticker)
        engine.addSystem(gravity)
        engine.addSystem(wind)
        engine.addSystem(clouds)
        engine.addSystem(renderer)
        engine.addSystem(debugRenderer)
        engine.addSystem(serializationSystem)
        engine.addSystem(rain)
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
}
