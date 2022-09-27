package com.hvz.models

import com.hvz.misc.GameState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int,

    @Column(name = "name")
    val gameName: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "game_state", nullable = false)
    val gameState: GameState,

    @Column(name = "nw_lat", nullable = false)
    val nwLat: Double,

    @Column(name = "nw_lng", nullable = false)
    val nwLng: Double,

    @Column(name = "se_lat", nullable = false)
    val seLat: Double,

    @Column(name = "se_lng", nullable = false)
    val seLng: Double,

    @OneToMany
    @JoinColumn(name = "player_ids")
    val players: Collection<Player>,

) {
    data class ReadDTO(val id: Int, val gameName: String,
        val description: String, val gameState: String,
        val nwLat: Double, val nwLng: Double,
        val seLat: Double, val seLng: Double,
        val players: Collection<Int>
    )

    fun toReadDto() = ReadDTO(id, gameName,
        description, gameState.name,
        nwLat, nwLng,
        seLat, seLng,
        players.map { it.id }
    )
}