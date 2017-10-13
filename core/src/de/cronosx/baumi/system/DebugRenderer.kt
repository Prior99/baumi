package de.cronosx.baumi.system

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.ashley.core.EntitySystem
import de.cronosx.baumi.component.*
import de.cronosx.baumi.data.debug
import de.cronosx.baumi.system.tick.*
import de.cronosx.baumi.Math.*
import ktx.ashley.mapperFor
import ktx.log.*

class DebugRenderer(var shapeRenderer: ShapeRenderer) : EntitySystem() {
    val positions = mapperFor<Position>()
    val branches = mapperFor<Branch>()
    val fruits = mapperFor<Fruit>()

    override fun update(delta: Float) {
        if (!debug.enableDebugRendering) return
        for (entity in engine.entities) {
            if (!positions.has(entity)) {
                continue
            }
            val position = positions.get(entity)
            val x = position.position.x
            val y = position.position.y
            shapeRenderer.begin(ShapeType.Filled)
            shapeRenderer.setColor(1f, 0f, 0f, 1f)
            shapeRenderer.x(x, y, 4f)
            shapeRenderer.end()
            if (branches.has(entity)) {
                val targetPosition = getChildPosition(position, branches.get(entity), 1f)
                shapeRenderer.begin(ShapeType.Line)
                shapeRenderer.setColor(0f, 1f, 0f, 1f)
                shapeRenderer.line(x, y, targetPosition.x, targetPosition.y)
                shapeRenderer.end()
                shapeRenderer.begin(ShapeType.Filled)
                shapeRenderer.setColor(0f, 0f, 1f, 1f)
                shapeRenderer.x(x, y, 4f)
                shapeRenderer.end()
            }
            if (fruits.has(entity)) {
                shapeRenderer.begin(ShapeType.Filled)
                shapeRenderer.setColor(1f, 1f, 0f, 1f)
                shapeRenderer.x(x, y, 4f)
                shapeRenderer.end()
            }
        }
    }
}
