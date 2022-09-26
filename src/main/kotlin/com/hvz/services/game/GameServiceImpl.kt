package com.hvz.services.game

import com.hvz.exceptions.GameNotFoundException
import com.hvz.models.Game
import com.hvz.repositories.GameRepository
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(val gameRepository: GameRepository) : GameService {

    override fun findById(id: Int): Game = gameRepository.findById(id)
        .orElseThrow {
            GameNotFoundException(id)
        }

    override fun findAll(): Collection<Game> = gameRepository.findAll()

    override fun add(entity: Game) {
        gameRepository.save(entity)
    }

    override fun update(entity: Game) {
        gameRepository.save(entity)
    }

    override fun deleteById(id: Int) = gameRepository.deleteById(id)
}