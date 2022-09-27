package com.hvz.models

import com.hvz.misc.GameState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity(name = "games")
data class Game(

    @Column(name = "name")
    val gameName: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "nw_lat", nullable = false)
    val nwLat: Double,

    @Column(name = "nw_lng", nullable = false)
    val nwLng: Double,

    @Column(name = "se_lat", nullable = false)
    val seLat: Double,

    @Column(name = "se_lng", nullable = false)
    val seLng: Double,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = -1
) {
    @Column(name = "game_state", nullable = false)
    lateinit var gameState: GameState

    @OneToMany(mappedBy = "game")
    val players: Collection<Player> = setOf()

    @OneToMany(mappedBy = "game")
    val kills: Collection<Kill> = setOf()

    fun toReadDto() = GameReadDTO(id, gameName,
        description, gameState.name,
        nwLat, nwLng,
        seLat, seLng,
        players.map { it.id },
        kills.map { it.id }
    )
}

data class GameReadDTO(val id: Int, val gameName: String,
                       val description: String, val gameState: String,
                       val nwLat: Double, val nwLng: Double,
                       val seLat: Double, val seLng: Double,
                       val players: Collection<Int>,
                       val kills: Collection<Int>,
)

data class GameAddDTO(val gameName: String, val description: String,
                      val nwLat: Double, val nwLng: Double,
                      val seLat: Double, val seLng: Double,
) {
    fun toEntity() = Game(
        gameName, description,
        nwLat, nwLng,
        seLat, seLng
    ).apply {
        gameState = GameState.REGISTERING
    }
}

data class GameEditDTO(val id: Int, val gameName: String,
                       val description: String,
                       val gameState: String,
                       val nwLat: Double, val nwLng: Double,
                       val seLat: Double, val seLng: Double,
)
