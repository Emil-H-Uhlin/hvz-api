package com.hvz.models.dtos

data class GameDTO(
    val id: Int,
    val gameName: String,
    val description: String,
    val gameState: String,
    val nwLat: Double,
    val nwLng: Double,
    val seLat: Double,
    val seLng: Double,
    val players: Collection<Int>
)