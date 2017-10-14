package de.cronosx.baumi.component

import com.badlogic.ashley.core.Component
import ktx.ashley.*
import de.cronosx.baumi.data.*
import kotlinx.serialization.*

@Serializable
class Genetic(
    var dna: DNA = defaultDna
) : Component
