package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*
import de.cronosx.baumi.data.*

class Branch(
        var rotation: Float = 0f,
        var length: Float = 0f,
        var maxLength: Float = 0f,
        var generation: Int = 0,
        var leafProbability: Float = 0f,
        var children: MutableList<Entity> = ArrayList(),
        var dna: DNA = defaultDna
) : Component {
    val branches = mapperFor<Branch>()
    val leafs = mapperFor<Leaf>()

    fun getMaxGeneration(): Int {
        val childBranches = children.filter{ branches.has(it) }.map{ branches.get(it) }
        if (childBranches.count() == 0) {
            return generation
        }
        return childBranches.map{ it.getMaxGeneration() }.max()?.toInt() ?: generation
    }

    fun getMaxLeafs(): Int {
        val maxDepth = getMaxGeneration()
        val maxLeafCount = Math.floor(dna.maxLeafCount * (generation.toDouble() / maxDepth.toDouble())).toInt()
        return maxLeafCount
    }
}
