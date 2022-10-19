package com.hvz.models

import com.hvz.misc.GameState
import javax.persistence.CascadeType
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

    @Column(name = "max_players", nullable = false)
    val maxPlayers: Int = DEFAULT_PLAYER_MAX_COUNT,

    @Column(name = "game_state", nullable = false)
    val gameState: GameState = GameState.REGISTERING,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = -1
) {
    companion object {
        const val DEFAULT_PLAYER_MAX_COUNT: Int = 32
    }

    @OneToMany(mappedBy = "game", cascade = [CascadeType.REMOVE])
    val players: Collection<Player> = setOf()

    @OneToMany(mappedBy = "game")
    val kills: Collection<Kill> = setOf()

    @OneToMany(mappedBy = "game")
    val messages: Collection<ChatMessage> = setOf()

    @OneToMany(mappedBy = "game")
    val missions: Collection<Mission> = setOf()

    fun toReadDto() = GameReadDTO(id, gameName,
        description, gameState.name,
        nwLat, nwLng,
        seLat, seLng,
        maxPlayers,
        players.map { "/api/v1/games/$id/players/${it.id}" },
        kills.map { "/api/v1/games/$id/kills/${it.id}" },
        messages.map { "/api/v1/games/$id/messages/${it.id}" },
        missions.map { "/api/v1/games/$id/missions/${it.id}" }
    )
}

data class GameReadDTO(val id: Int, val gameName: String,
                       val description: String, val gameState: String,
                       val nwLat: Double, val nwLng: Double,
                       val seLat: Double, val seLng: Double,
                       val maxPlayers: Int,
                       val players: Collection<String>,
                       val kills: Collection<String>,
                       val messages: Collection<String>,
                       val missions: Collection<String>,
                       val playerCount: Int = players.size,
)

data class GameAddDTO(val gameName: String, val description: String,
                      val nwLat: Double, val nwLng: Double,
                      val seLat: Double, val seLng: Double,
                      val maxPlayers: Int?,
) {
    fun toEntity() = Game(
        gameName, description,
        nwLat, nwLng,
        seLat, seLng,
        maxPlayers ?: Game.DEFAULT_PLAYER_MAX_COUNT
    )
}

data class GameEditDTO(val id: Int, val gameName: String,
                       val description: String,
                       val gameState: String,
                       val nwLat: Double, val nwLng: Double,
                       val seLat: Double, val seLng: Double,
)
