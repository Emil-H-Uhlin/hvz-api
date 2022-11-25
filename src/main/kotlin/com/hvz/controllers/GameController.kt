package com.hvz.controllers

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
    @DeleteMapping("games/{id}")
    fun deleteById(@PathVariable id: Int): ResponseEntity<Any> =
        when (gameService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> {
                gameService.deleteById(id)

                ResponseEntity.noContent().build()
            }
        }

    @PostMapping("games")
    fun addGame(@RequestBody dto: GameAddDTO): ResponseEntity<Any> =
        with (gameService.add(dto.toEntity())) {
            val uri = URI.create("api/v1/games/${id}")

            return ResponseEntity.created(uri).build()
        }

    @GetMapping("games")
    fun findAll() = ResponseEntity.ok(gameService.findAll().map { it.toReadDto() })

    @GetMapping("games/{id}")
    fun findById(@PathVariable id: Int): ResponseEntity<Any> =
        when (val game = gameService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(game.toReadDto())
        }

    @PutMapping("games/{id}")
    fun update(@PathVariable id: Int,
               @RequestBody dto: GameEditDTO): ResponseEntity<Any> {

        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return when (val game = gameService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> {
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
            }
        }
    }

    @GetMapping("currentUser/games")
    fun findByUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> =
        when (val user = userService.getUserBySub(jwt.claims["sub"] as String)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(gameService.findAll().filter { game ->
                user.players.any {
                    it.game!!.id == game.id
                }
            }.map { it.toReadDto() })
        }
}
