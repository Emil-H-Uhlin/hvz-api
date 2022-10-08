package com.hvz.models

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "hvz_user")
data class User(
        private val __uid : String,
        val name: String,
        val email: String,
) {
    @Id
    var uid: String = __uid.removePrefix("auth0|")

    @OneToMany(mappedBy = "user")
    val players: Collection<Player> = setOf()

    fun toReadDto() = UserReadDTO(uid, name, email, players.map { "/api/v1/games/${it.game!!.id}/${it.id}" })
}

data class UserReadDTO(val uid: String,
                       val name: String,
                       val email: String,
                       val players: Collection<String>,
)