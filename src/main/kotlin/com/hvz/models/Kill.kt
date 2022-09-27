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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = -1
) {
    @Column(name = "time_of_kill")
    val killTime = Timestamp.from(Instant.now())

    @ManyToOne
    @JoinColumn(name = "killer_id")
    lateinit var killer: Player

    @OneToOne
    @JoinColumn(name = "victim_id")
    lateinit var victim: Player

    @ManyToOne
    @JoinColumn(name = "game_id")
    lateinit var game: Game
}