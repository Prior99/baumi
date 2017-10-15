package de.cronosx.baumi.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Touchable.enabled
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.utils.*
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

class Game (val stage: Stage, val batch: Batch) : KtxScreen {
    val engine = PooledEngine()
    var uiVisible = false
    val events = Events()
    val serializationSystem = SerializationSystem()
    val shapeRenderer = ShapeRenderer()
    val ticker = Ticker()

    val view = table {
        setFillParent(true)
        touchable = enabled
        onClick {
            uiVisible = !uiVisible
            if (uiVisible) {
                stage.addActor(ui)
            } else {
                ui.remove()
            }
        }
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
        stage.addActor(view)
        stage.addActor(ui)
        Gdx.input.inputProcessor = stage

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
        if (uiVisible) {
            ui.remove()
        }
    }
}
