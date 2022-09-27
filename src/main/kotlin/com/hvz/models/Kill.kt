package com.hvz.models

import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity(name = "kills")
data class Kill(
    val story: String,

    @Column(name = "kill_lat", nullable = false)
    val lat: Double,

    @Column(name = "kill_lng", nullable = false)
    val lng: Double,

    @Column(name = "time_of_kill")
    val killTime: Timestamp = Timestamp.from(Instant.now()),

    @ManyToOne
    @JoinColumn(name = "killer_id")
    val killer: Player? = null,

    @OneToOne
    @JoinColumn(name = "victim_id")
    val victim: Player? = null,

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = -1
) {

    fun toReadDto() = KillReadDTO(
        id, story, lat, lng,
        killer?.id ?: -1, victim?.id ?: -1,
        game?.id ?: -1
    )
}

data class KillAddDTO(val story: String, val lat: Double,
                      val lng: Double, val victimBiteCode: String,
                      val killerId: Int,
)

data class KillEditDTO(val id: Int, val story: String, val lat: Double, val lng: Double)

data class KillReadDTO(val id: Int, val story: String,
                       val lat: Double, val lng: Double,
                       val killerId: Int, val victimId: Int,
                       val gameId: Int,
)