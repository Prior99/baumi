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
        var dna: DNA = defaultDna,
        var maxStorage: Float = 0f,
        var storage: Float = 0f,
        var maxHealth: Float = 0f,
        var health: Float = 0f
) : Component {
    val branches = mapperFor<Branch>()
    val leafs = mapperFor<Leaf>()

    fun childBranches() {
        return children.filter{ branches.has(it) }.map{ branches.get(it) }
    }

    fun childLeafs() {
        return children.filter{ leafs.has(it) }.map{ leafs.get(it) }
    }

    fun getUpkeepDemand(): Float {
        val childBranchUpKeep = childBranches().map{ it.getUpkeepDemand() }.sum()
        return dna.upKeep + childLeafs().count() * dna.leafUpKeep + childBranchUpKeep
    }

    fun dead(): Boolean {
        return health <= 0
    }

    fun getMaxGeneration(): Int {
        val children = childBranches()
        if (children.count() == 0) {
            return generation
        }
        return children.map{ it.getMaxGeneration() }.max()?.toInt() ?: generation
    }

    fun getMaxLeafs(): Int {
        val maxDepth = getMaxGeneration()
        val maxLeafCount = Math.floor(dna.maxLeafCount * (generation.toDouble() / maxDepth.toDouble())).toInt()
        return maxLeafCount
    }
}
