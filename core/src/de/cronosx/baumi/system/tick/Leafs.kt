package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Leafs(engine: Engine) : TickSubSystem(engine) {
    val decomposes = mapperFor<Decompose>()
    val leafs = mapperFor<Leaf>()
    val branches = mapperFor<Branch>()
    val movables = mapperFor<Movable>()

    override fun tick(number: Int) {
        engine.entities
                .filter {decomposes.has(it) && leafs.has(it) && movables.has(it) }
                .forEach { entity ->
            val decompose = decomposes.get(entity)
            val movable = movables.get(entity)
            val leaf = leafs.get(entity)
            if (decompose.current > 3f) {
                movable.floating = false
                movable.fixed = false
                if (leaf.parent != null) {
                    val parentBranch = branches.get(leaf.parent)
                    parentBranch.children.remove(entity)
                    leaf.parent = null
                }
            }
        }
    }
}