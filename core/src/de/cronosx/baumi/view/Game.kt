package de.cronosx.baumi.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Touchable.enabled
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.math.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.*
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.math.*
import ktx.log.*
import ktx.actors.*
import de.cronosx.baumi.system.*
import de.cronosx.baumi.system.renderer.*
import de.cronosx.baumi.system.tick.Ticker
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import de.cronosx.baumi.*
import com.badlogic.gdx.input.GestureDetector.*
import com.badlogic.gdx.input.*
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val engine = PooledEngine()
    val events = Events()
    val serializationSystem = SerializationSystem()
    val shapeRenderer = ShapeRenderer()
    val ticker = Ticker()
    val gestureListener = GameGestureListener()
    val input = GameInputAdapter()

    class GameInputAdapter : InputAdapter() {
        override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean { return false }
        override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean { return false }
    }

    class GameGestureListener : GestureListener {
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

    val ui = table {
        setFillParent(true)
        table {
            right()
            imageButton(style = "watering") {
                onClick {
                    events.wateringCan()
                }
            }.cell(
                height = appWidth / 6f,
                width = appWidth / 6f,
                pad = 20f
            )
        }.cell(
            expand = true,
            row = true,
            fillX = true,
            align = Align.bottom
        )
        pack()
    }

    override fun show() {
        engine.addSystem(ticker)
        engine.addSystem(Gravity())
        engine.addSystem(Wind())
        engine.addSystem(events)
        engine.addSystem(Clouds())
        engine.addSystem(Renderer(batch, ticker))
        engine.addSystem(DebugRenderer(shapeRenderer))
        engine.addSystem(serializationSystem)
        engine.addSystem(Rain())
        stage.addActor(view)
        stage.addActor(ui)
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
        ui.remove()
    }
}
