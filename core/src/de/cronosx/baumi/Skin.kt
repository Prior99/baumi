package de.cronosx.baumi

import com.badlogic.gdx.Gdx
import ktx.style.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.*
import ktx.actors.*

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
    // Font.
    val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/amatic/AmaticSC-Regular.ttf"))
    val font16 = {
        val fontParameters = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameters.size = 80
        fontParameters.color = Color(0.2f, 0.2f, 0.8f, 0.5f)
        fontGenerator.generateFont(fontParameters)
    }()
    val font12 = {
        val fontParameters = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameters.size = 60
        fontParameters.color = Color(0.2f, 0.2f, 0.8f, 0.5f)
        fontGenerator.generateFont(fontParameters)
    }()
    // Label.
    val defaultLabelStyle = Label.LabelStyle()
    defaultLabelStyle.font = font16
    defaultLabelStyle.fontColor = Color.BLACK
    skin.add("default", defaultLabelStyle)
    // Label Small.
    val smallLabelStyle = Label.LabelStyle()
    smallLabelStyle.font = font12
    smallLabelStyle.fontColor = Color.BLACK
    skin.add("small", smallLabelStyle)
    // Button.
    val defaultButtonStyle = Button.ButtonStyle()
    skin.add("default", defaultButtonStyle)
    // Scrollpane.
    val defaultScrollPaneStyle = ScrollPane.ScrollPaneStyle()
    skin.add("default", defaultScrollPaneStyle)
}
