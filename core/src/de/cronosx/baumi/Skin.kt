package de.cronosx.baumi

import ktx.style.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.*
import ktx.actors.*

val textureWatering = Texture("watering-can.png")

fun buttonUpRegion(texture: Texture): Drawable {
    return TextureRegionDrawable(
        TextureRegion(texture, 0, 0, texture.width / 2, texture.height)
    )
}

fun buttonDownRegion(texture: Texture): Drawable {
    return TextureRegionDrawable(
        TextureRegion(texture, texture.width / 2, 0, texture.width / 2, texture.height)
    )
}

fun createSkin(): Skin = skin() { skin ->
    imageButton("watering") {
        imageUp = buttonUpRegion(textureWatering)
        imageDown = buttonDownRegion(textureWatering)
    }
}
