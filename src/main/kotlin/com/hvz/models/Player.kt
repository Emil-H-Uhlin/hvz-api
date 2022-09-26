package com.hvz.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    val id: Int,

    @Column(name = "is_human", nullable = false)
    val human: Boolean,

    @Column(name = "is_patient_zero", nullable = false)
    val patientZero: Boolean,

    @Column(name = "bite_code", nullable = false, unique = true)
    val biteCode: String,
)