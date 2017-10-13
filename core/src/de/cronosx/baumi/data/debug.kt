package de.cronosx.baumi.data

import com.badlogic.gdx.Application.*

data class Debug (
    val disableRendering: Boolean,
    val enableDebugRendering: Boolean,
    val logLevel: Int,
    val extremeSpeed: Boolean,
    val infiniteBuffers: Boolean
)

val debug = Debug(
    disableRendering = false,
    enableDebugRendering = false,
    logLevel = LOG_INFO, // LOG_INFO, LOG_DEBUG
    extremeSpeed = false,
    infiniteBuffers = false
)
