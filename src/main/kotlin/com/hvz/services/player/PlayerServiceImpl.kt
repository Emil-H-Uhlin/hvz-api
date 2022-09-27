package com.hvz.services.player

import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.models.Player
import com.hvz.repositories.PlayerRepository
import org.springframework.stereotype.Service

@Service
class PlayerServiceImpl(val playerRepository: PlayerRepository): PlayerService {
    override fun findByBiteCode(biteCode: String) = findAll().find { it.biteCode == biteCode }

    override fun findById(id: Int): Player = playerRepository.findById(id)
        .orElseThrow {
            PlayerNotFoundException(id)
        }

    override fun findAll(): Collection<Player> = playerRepository.findAll()

    override fun add(entity: Player): Player {
        return playerRepository.save(entity)
    }

    override fun update(entity: Player) {
        playerRepository.save(entity)
    }

    override fun deleteById(id: Int) = playerRepository.deleteById(id)
}