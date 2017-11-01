import com.badlogic.gdx.Gdx
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.cronosx.baumi.data.config
import java.util.*

class Shop (
        var tick: Int,
        var lastTick: Double,
        var prices: MutableList<Int>,
        var velocities: MutableList<Int>,
        var debugCalendar: Calendar? = null
) {
    constructor() : this(0, 0.0, mutableListOf(), mutableListOf()) {
        val file = Gdx.files.local("shop.json")
        if (!file.exists()) {
            val calendar = Calendar.getInstance()
            calendar.set(2017, 1, 1, 0, 0, 0)
            tick = 0
            lastTick = calendar.timeInMillis / 1000.0
            prices = mutableListOf(1000)
            velocities = mutableListOf(0)
        }
        val parser = JsonParser()
        val obj = parser.parse(file.readString())
        tick = obj["tick"].int
        lastTick = obj["lastTick"].double
        prices = obj["prices"].array.map{ it.int }.toMutableList()
        velocities = obj["velocities"].array.map{ it.int }.toMutableList()
    }

    val currentVelocity: Int
        get() = velocities.last()

    val currentPrice: Int
        get() = prices.last()

    fun toJson(): JsonObject {
        return jsonObject(
                "tick" to tick,
                "lastTick" to lastTick,
                "prices" to jsonArray(prices),
                "velocities" to jsonArray(velocities)
        )
    }

    fun save() {
        Gdx.files.local("shop.json").writeString(toJson().toString(), false)
    }

    fun update() {
        val calendar = debugCalendar?.let { debugCalendar } ?: Calendar.getInstance()
        val now = calendar.timeInMillis.toDouble() / 1000.0
        var changed = false
        while (lastTick <= now - config.shopTickInterval) {
            val rand = Random(tick.toLong())
            prices.add(currentPrice + currentVelocity + rand.nextInt(10) - 5)
            velocities.add(currentVelocity + rand.nextInt(10) - 5)
            tick++
            lastTick += config.shopTickInterval
            changed = true
        }
        if (changed) {
            save()
        }
    }
}

var shop = Shop()

