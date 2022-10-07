package com.hvz.models

import org.springframework.security.oauth2.jwt.Jwt
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "hvz_user")
data class User(
    @Id
    val uid: String,
    val name: String,
    val email: String,
) {
    @OneToMany(mappedBy = "user")
    val players: Collection<Player> = setOf()

    fun toReadDto() = UserReadDTO(uid, name, email, players.map { it.id })
}

data class UserAddDTO(val name: String, val email: String) {
    fun toEntity(jwt: Jwt) = User(jwt.claims["sub"] as String, name, email)
}

data class UserReadDTO(val uid: String,
                       val name: String,
                       val email: String,
                       val players: Collection<Int>,
)