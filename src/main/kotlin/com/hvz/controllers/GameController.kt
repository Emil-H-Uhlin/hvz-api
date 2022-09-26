package com.hvz.controllers

import com.hvz.models.Game
import com.hvz.services.game.GameService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["api/v1/"])
class GameController(val gameService: GameService) {

    @GetMapping("games")
    fun findAll() = gameService.findAll()

    @GetMapping("games/{id}")
    fun findById(@PathVariable id: Int) = gameService.findById(id)

    @PutMapping("games/{id}")
    fun update(@PathVariable id: Int, @RequestBody game: Game) = gameService.update(game)

    @DeleteMapping("games/{id}")
    fun deleteById(@PathVariable id: Int) = gameService.deleteById(id)

    @PostMapping("games")
    fun addGame(@RequestBody game: Game) = gameService.add(game)
}