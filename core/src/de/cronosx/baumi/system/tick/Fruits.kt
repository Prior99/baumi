package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Engine
import ktx.ashley.*

class Fruits(engine: Engine) : TickSubSystem(engine) {
    val fruits = mapperFor<Fruit>()
    val consumers = mapperFor<Consumer>()
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()

    override fun tick(number: Int) {
        for (entity in engine.entities) {
            if (!fruits.has(entity)) {
                continue
            }
            val fruit = fruits.get(entity)
            val consumer = consumers.get(entity)
            val fruitGene = genetics.get(fruit.parent).dna.fruits
            if (consumer.energy > 0) {
                fruit.age++
            }
            val duration = fruitGene.growingDuration + fruitGene.bloomingDuration + fruitGene.fruitDuration
            if (fruit.age > duration) {
                branches.get(fruit.parent).children.remove(entity)
                engine.removeEntity(entity)
            }
        }
    }
}
