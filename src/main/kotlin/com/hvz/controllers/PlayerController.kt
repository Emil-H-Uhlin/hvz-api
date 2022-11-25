package com.hvz.controllers

import com.hvz.misc.GameState
import com.hvz.models.Player
import com.hvz.models.PlayerAddDTO
import com.hvz.models.PlayerEditDTO
import com.hvz.services.game.GameService
import com.hvz.services.player.PlayerService
import com.hvz.services.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class PlayerController(private val playerService: PlayerService,
                       private val gameService: GameService,
                       private val userService: UserService,
) {
    @GetMapping("players")
    fun findAll() = ResponseEntity.ok(playerService.findAll().map { it.toReadDto() })

    @GetMapping("players/{id}")
    fun findById(@PathVariable id: Int) : ResponseEntity<Any> =
        when (val player = playerService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(player.toReadDto())
        }

    @PutMapping("players/{id}")
    fun update(@PathVariable id: Int,
               @RequestBody dto: PlayerEditDTO): ResponseEntity<Any> {

        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return when (val player = playerService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> {
                playerService.update(
                    player.copy(
                        human = dto.human,
                    )
                )

                ResponseEntity.noContent().build()
            }
        }
    }

    @DeleteMapping("players/{id}")
    fun deleteById(@PathVariable id: Int): ResponseEntity<Any> =
        when (playerService.findById(id)) {
            null -> ResponseEntity.badRequest().build()
            else -> {
                playerService.deleteById(id)
                ResponseEntity.noContent().build()
            }
        }

    @PostMapping("games/{game_id}/players")
    fun addPlayer(@PathVariable(name = "game_id") gameId: Int,
                  @RequestBody dto: PlayerAddDTO,
                  @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

        val game = gameService.findById(gameId) ?: return ResponseEntity.notFound().build()

        if (game.gameState != GameState.REGISTERING || game.players.size >= game.maxPlayers)
            return ResponseEntity.badRequest().build()

        return when (val user = userService.getUserBySub(jwt.claims["sub"] as String)) {
            null -> ResponseEntity.badRequest().body("User not registered")
            else -> {
                if (user.players.any { p -> p.game!!.id == gameId })
                    return ResponseEntity.badRequest().body("User already playing game")

                val player = playerService.add(dto.toEntity().copy(game = game, user = user))

                val uri = URI.create("api/v1/players/${player.id}")

                ResponseEntity.created(uri).build()
            }
        }
    }

    @GetMapping("games/{game_id}/players")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> =
        when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(game.players.map { it.toReadDto() })
        }

    @GetMapping("games/{game_id}/players/{player_id}")
    fun findAll(@PathVariable(name = "game_id") gameId: Int,
                @PathVariable(name = "player_id") playerId: Int): ResponseEntity<Any> =
        when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> when (val player = game.players.find { p -> p.id == playerId }) {
                null -> ResponseEntity.notFound().build()
                else -> ResponseEntity.ok(player.toReadDto())
            }
        }
    
    @GetMapping("games/{game_id}/currentUser/player")
    fun getUserPlayer(@PathVariable(name = "game_id") gameId: Int,
                      @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> =
        when (val user = userService.getUserBySub(jwt.claims["sub"] as String)) {
            null -> ResponseEntity.badRequest().body("User not registered")
            else -> when (val player = user.players.find { p -> p.game!!.id == gameId }) {
                null -> ResponseEntity.notFound().build()
                else -> ResponseEntity.ok(player.toReadDto())
            }
        }
}