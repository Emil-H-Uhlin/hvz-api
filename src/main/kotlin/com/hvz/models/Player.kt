package com.hvz.models

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "players")
data class Player(

    @Column(name = "is_human", nullable = false)
    val human: Boolean,

    @Column(name = "is_patient_zero", nullable = false)
    val patientZero: Boolean,

    @Column(name = "bite_code", nullable = false, unique = true)
    val biteCode: String,

    @ManyToOne
    @JoinColumn(name = "game")
    var game: Game? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = -1
) {
    fun toReadDto() = PlayerReadDTO(id, human, patientZero, biteCode, game?.id ?: -1)
}

data class PlayerReadDTO(val id: Int, val human: Boolean,
                         val patientZero: Boolean, val biteCode: String,
                         val game: Int,
)

data class PlayerAddDTO(val human: Boolean) {
    fun toEntity() = Player(
        human,
        !human,
        UUID.randomUUID().toString()
    )
}

data class PlayerEditDTO(val id: Int, val human: Boolean)