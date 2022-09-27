package com.hvz.services.player

import com.hvz.models.Player
import com.hvz.services.CrudService

interface PlayerService : CrudService<Player, Int> {
    fun findByBiteCode(biteCode: String): Player
}