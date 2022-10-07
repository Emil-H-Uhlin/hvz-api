package com.hvz.models

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "hvz_user")
data class User(
    @Id
    val uid: String,
) {
    @OneToMany(mappedBy = "user")
    val players: Collection<Player> = setOf()
}