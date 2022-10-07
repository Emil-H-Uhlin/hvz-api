package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.UserNotFoundException
import com.hvz.models.UserAddDTO
import com.hvz.services.game.GameService
import com.hvz.services.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping
@CrossOrigin(origins = ["*"])
class UserController(private val userService: UserService,
                     private val gameService: GameService
) {

    @DeleteMapping("api/v1/users/{id}")
    fun deleteById(@PathVariable(name = "id") uid: String): ResponseEntity<Any> {

        return try {
            userService.findById(uid)
            userService.deleteById(uid)

            ResponseEntity.noContent().build()
        } catch (userNotFoundException: UserNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/register")
    fun addUser(@AuthenticationPrincipal jwt: Jwt,
                @RequestBody dto: UserAddDTO): ResponseEntity<Any> {

        val user = userService.add(dto.toEntity(jwt))

        val uri = URI.create("api/v1/users/${user.uid}")

        return ResponseEntity.created(uri).build()
    }

    @GetMapping("api/v1/users")
    fun findAll() = ResponseEntity.ok(userService.findAll().map { it.toReadDto() })

    @GetMapping("api/v1/users/{id}")
    fun findById(@PathVariable(name = "id") uid: String): ResponseEntity<Any> {

        return try {
            val user = userService.findById(uid)
            return ResponseEntity.ok(user.toReadDto())
        } catch (userNotFoundException: UserNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("api/v1/games/{game_id}/users")
    fun findByGameId(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            val users = userService.findAll().filter { user ->
                game.players.find { it.user!!.uid == user.uid } != null
            }

            return ResponseEntity.ok(users.map { it.toReadDto() })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}