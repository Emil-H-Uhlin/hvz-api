package com.hvz.controllers

import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.models.Player
import com.hvz.services.player.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
class PlayerController(val playerService: PlayerService) {
    @GetMapping("players")
    fun findAll() = ResponseEntity.ok(playerService.findAll())

    @GetMapping("players/{id}")
    fun findById(@PathVariable id: Int) : ResponseEntity<Any> {
        return try {
            val player = playerService.findById(id)
            ResponseEntity.ok(player)
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("players/{id}")
    fun update(@PathVariable id: Int, @RequestBody player: Player): ResponseEntity<Any> {
        if (player.id != id)
            return ResponseEntity.badRequest().build()

        return try {
            playerService.findById(id)
            playerService.update(player)

            ResponseEntity.noContent().build()
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("players/{id}")
    fun deleteById(@PathVariable id: Int): ResponseEntity<Any> {
        return try {
            playerService.findById(id)
            playerService.deleteById(id)

            ResponseEntity.noContent().build()
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("players")
    fun addPlayer(@RequestBody player: Player) = playerService.add(player)
}