package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.exceptions.UserNotFoundException
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

    //region Admin
    @GetMapping("players")
    fun findAll() = ResponseEntity.ok(playerService.findAll().map { it.toReadDto() })

    @GetMapping("players/{id}")
    fun findById(@PathVariable id: Int) : ResponseEntity<Any> {

        return try {
            val player = playerService.findById(id)
            ResponseEntity.ok(player.toReadDto())
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("players/{id}")
    fun update(@PathVariable id: Int,
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
    fun deleteById(@PathVariable id: Int): ResponseEntity<Any> {

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
    fun addPlayer(@PathVariable(name = "game_id") gameId: Int,
                  @RequestBody dto: PlayerAddDTO,
                  @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)
            val user = userService.findById((jwt.claims["sub"] as String).removePrefix("auth0|"))

            if (game.gameState != GameState.REGISTERING) {
                return ResponseEntity.badRequest().build()
            }

            if (user.players.any { p -> p.user!!.uid == user.uid})
                return ResponseEntity.badRequest().build()

            val player = playerService.add(dto.toEntity().copy(game = game, user = user))

            val uri = URI.create("api/v1/players/${player.id}")

            ResponseEntity.created(uri).build()
        } catch (_: GameNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (_: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("games/{game_id}/players")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            ResponseEntity.ok(game.players.map { it.toReadDto() })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("games/{game_id}/players/{player_id}")
    fun findAll(@PathVariable(name = "game_id") gameId: Int,
                @PathVariable(name = "player_id") playerId: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)
            val player = game.players.find { p -> p.id == playerId }
                ?: return ResponseEntity.notFound().build()

            ResponseEntity.ok(player.toReadDto())
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("games/{game_id}/players?user")
    fun findByUser(@PathVariable(name = "game_id") gameId: Int,
                   @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

        return try {
            val user = userService.findById(jwt.claims["sub"] as String)

            return ResponseEntity.ok(user.players.map { it.toReadDto() })
        }
        catch (userNotFoundException: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}