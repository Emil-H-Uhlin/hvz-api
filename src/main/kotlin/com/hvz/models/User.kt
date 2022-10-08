package com.hvz.models

import org.springframework.security.oauth2.jwt.Jwt
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

    fun toReadDto() = UserReadDTO(uid, name, email, players.map { it.id })
}

data class UserReadDTO(val uid: String,
                       val name: String,
                       val email: String,
                       val players: Collection<Int>,
)