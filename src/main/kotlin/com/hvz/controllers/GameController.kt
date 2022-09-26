package com.hvz.controllers

import com.hvz.services.game.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/"])
class GameController(val gameService: GameService) {

    @GetMapping("games")
    fun findAll() = gameService.findAll()
}