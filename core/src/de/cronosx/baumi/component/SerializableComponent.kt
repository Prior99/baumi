package de.cronosx.baumi.component

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import ktx.ashley.mapperFor

val uuids = mapperFor<Uuid>()

abstract class SerializableComponent : Component {
    abstract fun toJson(): JsonObject
}

fun deserializeComponent(obj: JsonObject, engine: Engine): Component? {
    return when (obj["type"].string) {
        "Age" -> Age(obj)
        "Branch" -> Branch(obj)
        "Buffer" -> Buffer(obj)
        "Cloud" -> Cloud(obj)
        "Consumer" -> Consumer(obj)
        "Decompose" -> Decompose(obj)
        "Fruit" -> Fruit(obj)
        "Genetic" -> Genetic(obj)
        "GroundWater" -> GroundWater(obj)
        "Health" -> Health(obj)
        "Leaf" -> Leaf(obj)
        "Movable" -> Movable(obj)
        "Position" -> Position(obj)
        "Producer" -> Producer(obj)
        "Root" -> Root(obj)
        "Uuid" -> Uuid(obj)
        "RainDrop" -> RainDrop(obj)
        "Draggable" -> Draggable(obj)
        "Cart" -> Cart(obj)
        "FertilizerBag" -> FertilizerBag(obj)
        "FertilizerGrain" -> FertilizerGrain(obj)
        "Child" -> Child(obj, engine)
        "Parent" -> Parent(obj, engine)
        else -> null
    }
}
