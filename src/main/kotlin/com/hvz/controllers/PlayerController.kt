package com.hvz.controllers

import com.hvz.models.Player
import com.hvz.services.player.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/"])
class PlayerController(val playerService: PlayerService) {
    @GetMapping("players")
    fun findAll() = playerService.findAll()

    @GetMapping("players/{id}")
    fun findById(@PathVariable id: Int): Player = playerService.findById(id)

    @PutMapping("players/{id}")
    fun update(@PathVariable id: Int, player: Player) = playerService.update(player)

    @DeleteMapping("players/{id}")
    fun deleteById(@PathVariable id: Int) = playerService.deleteById(id)

    @PostMapping("players")
    fun addPlayer(@RequestBody player: Player) = playerService.add(player)
}