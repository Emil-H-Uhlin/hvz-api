package com.hvz.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class Mission(
    @Column(name = "name", length = 80)
    val name: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "mission_lat")
    val lat: Double,

    @Column(name = "mission_lng")
    val lng: Double,

    @Id
    @GeneratedValue
    val id: Int = -1,

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game? = null,
) {
    fun toReadDto() = MissionReadDTO(id, name,
        description,
        lat, lng,
        game?.id ?: -1
    )
}

data class MissionAddDTO(val name: String, val description: String,
                         val lat: Double, val lng: Double,
)

data class MissionReadDTO(val id: Int, val name: String,
                          val description: String,
                          val lat: Double, val lng: Double,
                          val gameId: Int,
)

data class MissionEditDTO(val id: Int, val name: String,
                          val description: String,
                          val lat: Double, val lng: Double,
)

