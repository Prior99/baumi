package de.cronosx.baumi.system.tick

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.*
import ktx.ashley.entity
import ktx.ashley.mapperFor
import ktx.math.plus
import ktx.log.*

/**
 * This system distributes the produced amount of energy across all consumer entities.
 */
class EnergyDistribution(engine: Engine) : TickSubSystem(engine) {
    val healths = mapperFor<Health>()
    val consumers = mapperFor<Consumer>()
    val producers = mapperFor<Producer>()

    override fun tick(number: Int) {
        // Of course, we only care about living entities in the life() function :)
        val livingEntities = engine.entities
            .filter { !healths.has(it) || healths.get(it).alive }

        val producerEntities = livingEntities.filter { producers.has(it) }
        val consumerEntities = livingEntities.filter { consumers.has(it) }

        // Find out how much production and consumption we have
        val totalProduction = producerEntities
            .sumByDouble({ producers.get(it).rate.toDouble() }).toFloat()
        val totalConsumption = consumerEntities
            .sumByDouble({ consumers.get(it).rate.toDouble() }).toFloat()
        // This value controls that all consumers get less / more according to the production.
        val efficiency = totalProduction / totalConsumption
        // Log the current production and consumption as well as the efficiency.
        if (Gdx.app.logLevel == LOG_DEBUG) {
            val efficiencyString = "%.1f %%".format(efficiency * 100)
            debug {
                "Production:  $totalProduction (${producerEntities.count()} producers)\n" +
                        "Consumption: $totalConsumption (${consumerEntities.count()} consumers)\n" +
                        "Efficiency:  $efficiencyString"
            }
        }
        // Give everybody energy in proportion to their demand and availability
        var leftoverEnergy = 0f
        for (entity in consumerEntities) {
            val consumer = consumers.get(entity)
            val gain = consumer.rate * efficiency * consumer.effectiveness
            val upkeep = consumer.rate
            // Add the requested amount according to global efficiency and local effectiveness,
            // but always subtract the full rate too.
            consumer.energy = maxOf(0f, consumer.energy + gain - upkeep)
            // Deduct the leftover energy from all consumers.
            if (consumer.energy > consumer.maxEnergy) {
                leftoverEnergy += consumer.energy - consumer.maxEnergy
                consumer.energy = consumer.maxEnergy
                continue
            }
            // The consumer did not get enough energy and the health needs to be impacted.
            if (consumer.energy <= consumer.minEnergy && healths.has(entity)) {
                // If the energy sank below the minimum and the entity has health, impact it.
                // The rate of health loss is proportional to the amount of energy missing below the minimum energy.
                val health = healths.get(entity)
                val defecit = maxOf(0f, consumer.energy) / if (consumer.minEnergy == 0f) 1f else consumer.minEnergy
                val lossRate = 1f - defecit
                val loss = lossRate * health.max * consumer.healthDecayRate
                val before = health.current
                health.current = maxOf(0f, health.current - loss)
                debug { "Reducing health of entity by $loss from $before to ${health.current}." }
            }
        }
    }
}
