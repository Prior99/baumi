package de.cronosx.baumi.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.github.salomonbrys.kotson.int
import com.github.salomonbrys.kotson.obj
import com.google.gson.JsonParser
import de.cronosx.baumi.Application
import de.cronosx.baumi.data.config
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

class TreesMenu(val stage: Stage, val batch: Batch, val application: Application) : KtxScreen {
    val parser = JsonParser()
    val formatter = PeriodFormatterBuilder()
            .appendYears().appendSuffix(" year", " years").appendSeparator(", ")
            .appendDays().appendSuffix(" day", " days").appendSeparator(", ")
            .appendHours().appendSuffix(" hour", " hours").appendSeparator(", ")
            .appendMinutes().appendSuffix(" minute", " minutes").appendSeparator(", ")
            .appendSeconds().appendSuffix(" second", " seconds")
            .toFormatter()
    val view = table {
        setFillParent(true)
        background = whiteBackground
        table {
            scrollPane {
                table {
                    Gdx.files.local("trees/").list().map{ file ->
                        val obj = parser.parse(file.child("game.json").readString()).obj
                        val screenshot = TextureRegionDrawable(TextureRegion(Texture(file.child("screenshot.png"))))
                        val age = formatter.print(Period((obj["world"].obj["tick"].int / config.tickSpeed).toLong() * 1000))
                        table {
                            touchable = Touchable.enabled
                            align(Align.topLeft)
                            table {
                                background = screenshot
                                left()
                                cell(width = 216f, height = 384f)
                            }
                            table {
                                align(Align.topLeft)
                                left()
                                top()
                                cell(padLeft = 20f, fillX = true, fillY = true)
                                table {
                                    cell(fillX = true)
                                    left()
                                    label("A tree")
                                }
                                row()
                                table {
                                    cell(padTop = 40f)
                                    label(age, "small").cell(padRight = 20f)
                                }
                            }
                            onClick {
                                application.setScreen<Game>()
                                val game = application.context.inject<Game>()
                                if (!game.load(obj)) {
                                    file.delete()
                                }
                            }
                            cell(row = true, width = 1080f, height = 384f, padBottom = 40f)
                        }
                    }
                    row()
                    table {
                        cell(padTop = 40f, width = 1080f, row = true, fillX = true)
                        table {
                            cell(expandX = true)
                            button {
                                label("Back")
                                onClick { application.setScreen<MainMenu>() }
                            }
                        }
                        table {
                            cell(expandX = true)
                            button {
                                label("New Tree")
                                onClick {
                                    application.setScreen<Game>()
                                    val game = application.context.inject<Game>()
                                    game.newGame()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    inner class GameInputAdapter : InputAdapter() {
        override fun keyDown(keycode: Int): Boolean {
            if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
                application.setScreen<MainMenu>()
            }
            return false
        }
    }

    val input = GameInputAdapter()

    override fun show() {
        stage.addActor(view)
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(stage)
        multiplexer.addProcessor(input)
        Gdx.input.inputProcessor = multiplexer
        Gdx.input.isCatchBackKey = true
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }
}

