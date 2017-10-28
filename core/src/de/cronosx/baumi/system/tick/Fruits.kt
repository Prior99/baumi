package de.cronosx.baumi.system.tick

import de.cronosx.baumi.component.*
import com.badlogic.ashley.core.Engine
import de.cronosx.baumi.data.config
import ktx.ashley.*

class Fruits(engine: Engine) : TickSubSystem(engine) {
    val fruits = mapperFor<Fruit>()
    val consumers = mapperFor<Consumer>()
    val branches = mapperFor<Branch>()
    val genetics = mapperFor<Genetic>()
    val movables = mapperFor<Movable>()
    val positions = mapperFor<Position>()
    val carts = mapperFor<Cart>()
    val children = mapperFor<Child>()
    val parents = mapperFor<Parent>()

    override fun tick(number: Int) {
        val cartEntity = engine.entities.find { carts.has(it) && positions.has(it) }
        val cartPosition = positions.get(cartEntity).position
        val cart = carts.get(cartEntity)
        engine.entities
                .filter { fruits.has(it) && consumers.has(it) }
                .forEach {
                    val fruit = fruits.get(it)
                    val consumer = consumers.get(it)
                    val movable = movables.get(it)
                    val child = children.get(it)
                    val position = positions.get(it).position
                    val fruitGene = genetics.get(it).dna.fruits
                    if (consumer.energy > 0) {
                        fruit.age++
                    }
                    val duration = fruitGene.growingDuration + fruitGene.bloomingDuration + fruitGene.fruitDuration
                    if (fruit.age > duration && child.parent != null) {
                        parents.get(child.parent).children.remove(it)
                        child.parent = null
                        movable.floating = false
                        movable.fixed = false
                    }
                    val inCart = position.x >= cartPosition.x + 82 &&
                            position.x <= cartPosition.x + 325 &&
                            position.y <= cartPosition.y + 100
                    if (inCart && cart.content < config.maxCartContent) {
                        engine.removeEntity(it)
                        cart.content++
                    }
                    else if (position.y <= config.groundHeight) {
                        engine.removeEntity(it)
                    }
                }
    }
}
