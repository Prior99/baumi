package de.cronosx.baumi

import kotlin.reflect.KClass

object Bus {
    val map: MutableMap<KClass<Any>, MutableList<(event: Any) -> Unit>> = mutableMapOf()

    inline fun <reified T: Any> on(noinline handler: (event: T) -> Unit) {
        val eventClass = T::class as KClass<Any>
        if (map[eventClass] == null) {
            map[eventClass] = mutableListOf<(event: Any) -> Unit>()
        }
        map[eventClass]?.add(handler as (event: Any) -> Unit)
    }

    inline fun emit(event: Any) {
        map[event::class]?.forEach { it(event) }
    }

    inline fun reset() {
        map.clear()
    }
}