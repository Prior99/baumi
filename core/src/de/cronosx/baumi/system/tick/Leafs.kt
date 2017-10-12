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
        for (entity in engine.entities) {
            if (!decomposes.has(entity) || !leafs.has(entity) || !movables.has(entity)) {
                continue
            }
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
