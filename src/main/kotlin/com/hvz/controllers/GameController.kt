package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.UserNotFoundException
import com.hvz.misc.GameState
import com.hvz.models.GameAddDTO
import com.hvz.models.GameEditDTO
import com.hvz.services.game.GameService
import com.hvz.services.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class GameController(private val gameService: GameService,
                     private val userService: UserService,
) {

    //region Admin
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
    fun addGame(@RequestBody dto: GameAddDTO): ResponseEntity<Any> {
        val game = gameService.add(dto.toEntity())

        val uri = URI.create("api/v1/games/${game.id}")

        return ResponseEntity.created(uri).build()
    }
    //endregion

    @GetMapping("games")
    fun findAll() = ResponseEntity.ok(gameService.findAll().map { it.toReadDto() })

    @GetMapping("games/{id}")
    fun findById(@AuthenticationPrincipal token: Jwt,
                 @PathVariable id: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(id)
            ResponseEntity.ok(game.toReadDto())
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("games/{id}")
    fun update(@PathVariable id: Int,
               @RequestBody dto: GameEditDTO): ResponseEntity<Any> {

        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return try {
            val game = gameService.findById(id)

            gameService.update(
                game.copy(
                    gameName = dto.gameName,
                    description = dto.description,
                    nwLat = dto.nwLat,
                    nwLng = dto.nwLng,
                    seLat = dto.seLat,
                    seLng = dto.seLng,
                    gameState = GameState.valueOf(dto.gameState)
                )
            )

            ResponseEntity.noContent().build()
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("currentUser/games")
    fun findByUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {
        return try {
            val user = userService.findById((jwt.claims["sub"] as String).removePrefix("auth0|"))

            val games = gameService.findAll().filter { game ->
                user.players.find { it.game!!.id == game.id } != null
            }

            ResponseEntity.ok(games.map { it.toReadDto() })
        } catch (userNotFoundException: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}