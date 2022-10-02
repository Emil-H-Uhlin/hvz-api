package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.misc.GameState
import com.hvz.models.Player
import com.hvz.models.PlayerAddDTO
import com.hvz.models.PlayerEditDTO
import com.hvz.services.game.GameService
import com.hvz.services.player.PlayerService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class PlayerController(val playerService: PlayerService,
                       val gameService: GameService) {

    //region Admin
    @GetMapping("players")
    fun findAll(@AuthenticationPrincipal token: Jwt) = ResponseEntity.ok(playerService.findAll().map { it.toReadDto() })

    @GetMapping("players/{id}")
    fun findById(@AuthenticationPrincipal token: Jwt,
                 @PathVariable id: Int) : ResponseEntity<Any> {

        return try {
            val player = playerService.findById(id)
            ResponseEntity.ok(player.toReadDto())
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("players/{id}")
    fun update(@AuthenticationPrincipal token: Jwt,
               @PathVariable id: Int,
               @RequestBody dto: PlayerEditDTO): ResponseEntity<Any> {

        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return try {
            val player = playerService.findById(id)
            playerService.update(
                player.copy(
                    human = dto.human,
                )
            )

            ResponseEntity.noContent().build()
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("players/{id}")
    fun deleteById(@AuthenticationPrincipal token: Jwt,
                   @PathVariable id: Int): ResponseEntity<Any> {

        return try {
            playerService.findById(id)
            playerService.deleteById(id)

            ResponseEntity.noContent().build()
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }
    //endregion

    @PostMapping("games/{game_id}/players")
    fun addPlayer(@AuthenticationPrincipal token: Jwt,
                  @PathVariable(name = "game_id") gameId: Int,
                  @RequestBody dto: PlayerAddDTO): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            if (game.gameState != GameState.REGISTERING) {
                return ResponseEntity.badRequest().build()
            }

            val player = playerService.add(dto.toEntity().copy(game = game))

            val uri = URI.create("api/v1/players/${player.id}")

            ResponseEntity.created(uri).build()
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("games/{game_id}/players")
    fun findAll(@AuthenticationPrincipal token: Jwt,
                @PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            ResponseEntity.ok(game.players.map { it.toReadDto() })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

}