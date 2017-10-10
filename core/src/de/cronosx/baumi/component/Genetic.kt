package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*
import de.cronosx.baumi.data.*

class Genetic(
    var dna: DNA = defaultDna
) : Component {
}
