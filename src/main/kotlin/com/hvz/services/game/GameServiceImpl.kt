package com.hvz.services.game

import com.hvz.models.Game
import com.hvz.repositories.GameRepository
import org.springframework.stereotype.Service

@Service
class GameServiceImpl(val gameRepository: GameRepository) : GameService {

    override fun findById(id: Int): Game? = gameRepository.findById(id).let {
        return if (it.isPresent) it.get() else null
    }

    override fun findAll(): Collection<Game> = gameRepository.findAll()

    override fun add(entity: Game): Game {
        return gameRepository.save(entity)
    }

    override fun update(entity: Game) {
        gameRepository.save(entity)
    }

    override fun deleteById(id: Int) = gameRepository.deleteById(id)
}