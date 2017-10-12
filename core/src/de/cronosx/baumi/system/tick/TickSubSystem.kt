package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine

abstract class TickSubSystem(val engine: Engine) {
    abstract fun tick(number: Int);
}
