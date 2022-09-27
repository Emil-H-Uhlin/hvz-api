package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.misc.GameState
import com.hvz.models.Game
import com.hvz.services.game.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["api/v1/"])
class GameController(val gameService: GameService) {

    @GetMapping("games")
    fun findAll() = ResponseEntity.ok(gameService.findAll().map { it.toReadDto() })

    @GetMapping("games/{id}")
    fun findById(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            val game = gameService.findById(id)
            ResponseEntity.ok(game.toReadDto())
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("games/{id}")
    fun update(@PathVariable id: Int, @RequestBody dto: Game.EditDTO): ResponseEntity<Any> {
        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return try {
            val game = gameService.findById(id)

            gameService.update(
                Game(dto.gameName,
                    dto.description,
                    GameState.valueOf(dto.gameState),
                    dto.nwLat,
                    dto.nwLng,
                    dto.seLat,
                    dto.seLng,
                    game.players,
                    game.id)
            )

            ResponseEntity.noContent().build()
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("games/{id}")
    fun deleteById(@PathVariable id: Int): ResponseEntity<Any> {

        return try {
            gameService.findById(id)
            gameService.deleteById(id)

            ResponseEntity.noContent().build()
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("games")
    fun addGame(@RequestBody game: Game.AddDTO) = gameService.add(game.toEntity())
}