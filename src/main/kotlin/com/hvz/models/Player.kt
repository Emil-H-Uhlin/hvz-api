package com.hvz.models

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "players")
data class Player(

    @Column(name = "is_human", nullable = false)
    val human: Boolean,

    @Column(name = "is_patient_zero", nullable = false)
    val patientZero: Boolean = !human,

    @Column(name = "bite_code", nullable = false, unique = true)
    val biteCode: String? = null,

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = -1,
) {

    @OneToMany
    @JoinColumn(name = "message_ids")
    val messages: Collection<ChatMessage> = setOf()

    fun toReadDto() = PlayerReadDTO(id, human, patientZero,
        biteCode ?: "NO BITE CODE",
        game?.id ?: -1,
        messages.map { it.id }
    )
}

data class PlayerReadDTO(val id: Int, val human: Boolean,
                         val patientZero: Boolean, val biteCode: String,
                         val game: Int,
                         val messages: Collection<Int>
)

data class PlayerAddDTO(val human: Boolean) {
    fun toEntity(user: User, game: Game) = Player(
        human = human,
        biteCode = UUID.randomUUID().toString(),
        user = user,
        game = game
    )
}

data class PlayerEditDTO(val id: Int, val human: Boolean)