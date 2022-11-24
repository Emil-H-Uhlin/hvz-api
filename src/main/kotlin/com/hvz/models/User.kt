package com.hvz.models

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "hvz_user")
data class User(
        @Id
        val uid : String,
        val name: String,
        val email: String,
) {
    @OneToMany(mappedBy = "user")
    val players: Collection<Player> = setOf()

    fun toReadDto() = UserReadDTO(name, email)
}

data class UserReadDTO(val name: String, val email: String)