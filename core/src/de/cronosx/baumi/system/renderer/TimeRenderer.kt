package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import de.cronosx.baumi.data.config
import de.cronosx.baumi.data.world
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder

class TimeRenderer(val batch: Batch, engine: Engine) : RenderSubSystem(engine) {
    val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/amatic/AmaticSC-Regular.ttf"))
    val font16 = {
        val fontParameters = FreeTypeFontParameter()
        fontParameters.size = 50
        fontParameters.color = Color(0.2f, 0.2f, 0.8f, 0.5f)
        fontGenerator.generateFont(fontParameters)
    }()
    val formatter = PeriodFormatterBuilder()
            .appendYears().appendSuffix("y", "y").appendSeparator(" ")
            .appendDays().appendSuffix("d", "d").appendSeparator(" ")
            .appendHours().appendSuffix("h", "h").appendSeparator(" ")
            .appendMinutes().appendSuffix("m", "m").appendSeparator(" ")
            .appendSeconds().appendSuffix(" ", "s")
            .toFormatter()

    override fun render(delta: Float) {
        var period = Period((world.tick / config.tickSpeed).toLong() * 1000)
        font16.draw(batch, formatter.print(period), 10f, 1900f)
    }
}

