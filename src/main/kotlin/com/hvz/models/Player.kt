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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = -1
) {

    @ManyToOne
    @JoinColumn(name = "game_id")
    lateinit var game: Game

    @Column(name = "bite_code", nullable = false, unique = true)
    lateinit var biteCode: String

    @Column(name = "is_patient_zero", nullable = false)
    val patientZero = !human

    fun toReadDto() = PlayerReadDTO(id, human, patientZero, biteCode, game.id)
}

data class PlayerReadDTO(val id: Int, val human: Boolean,
                         val patientZero: Boolean, val biteCode: String,
                         val game: Int,
)

data class PlayerAddDTO(val human: Boolean) {
    fun toEntity() = Player(human).apply {
        biteCode = UUID.randomUUID().toString()
    }
}

data class PlayerEditDTO(val id: Int, val human: Boolean)