package de.cronosx.baumi.system.renderer

import com.badlogic.ashley.core.Engine
import de.cronosx.baumi.component.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.Texture
import ktx.ashley.*
import ktx.math.*
import ktx.log.*
import de.cronosx.baumi.Math.*
import de.cronosx.baumi.*
import de.cronosx.baumi.system.tick.Ticker

class LoadingRenderer(val batch: Batch, engine: Engine, val ticker: Ticker) : RenderSubSystem(engine) {
    val backgroundTexture = Texture("loading-background.png")
    val progressTexture = Texture("progressbar.png")
    val textureHeight = 154
    val textureWidth = 1004

    override fun render(delta: Float) {
        if (!ticker.replaying) {
            return
        }
        val progress = maxOf(ticker.replayed.toFloat() / ticker.totalReplayTicks.toFloat(), 0.05f)
        info { "$progress" }
        batch.draw(backgroundTexture, 0f, 0f)
        // Draw the background of the bar.
        val progressBg = TextureRegion(progressTexture, 0, textureHeight, textureWidth, textureHeight)
        val bgSprite = Sprite(progressBg)
        bgSprite.setCenter(appWidth / 2f, appHeight / 2f)
        bgSprite.draw(batch)
        // Draw the filled part of the bar.
        val progressFg = TextureRegion(progressTexture, 0f, 0f, progress, 0.5f)
        val fgSprite = Sprite(progressFg)
        fgSprite.setCenter(appWidth / 2f, appHeight / 2f)
        fgSprite.translateX(-(1f - progress) * textureWidth / 2f)
        fgSprite.draw(batch)
        // Draw the rounded corners of the end of the bar.
        /* val progressFgEnd = TextureRegion(progressTexture, textureWidth - 25, 0, 25, textureHeight) */
        /* val fgEndSprite = Sprite(progressFgEnd) */
        /* fgEndSprite.setCenter(appWidth / 2f, appHeight / 2f) */
        /* fgEndSprite.translateX(-textureWidth / 2f + progress * textureWidth + 12f) */
        /* fgEndSprite.draw(batch) */
    }
}

