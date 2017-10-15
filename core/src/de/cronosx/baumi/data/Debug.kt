package de.cronosx.baumi.data

import com.badlogic.gdx.Application.*

data class Debug (
    var disableRendering: Boolean,
    var enableDebugRendering: Boolean,
    var logLevel: Int,
    var extremeSpeed: Boolean,
    var infiniteBuffers: Boolean
)

val debug = Debug(
    disableRendering = false,
    enableDebugRendering = false,
    logLevel = LOG_DEBUG, // LOG_INFO, LOG_DEBUG
    extremeSpeed = false,
    infiniteBuffers = false
)
