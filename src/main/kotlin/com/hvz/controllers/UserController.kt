package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.UserNotFoundException
import com.hvz.models.User
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL

@RestController
@RequestMapping
@CrossOrigin(origins = ["*"])
class UserController(private val userService: UserService,
                     private val gameService: GameService,
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

    @PutMapping("/register")
    fun addUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

        val url = URL(jwt.getClaimAsStringList("aud")[1])
        val con = url.openConnection().apply {
            setRequestProperty("method", "POST")
            setRequestProperty("authorization", "Bearer ${jwt.tokenValue}")
        }

        val reader = BufferedReader(InputStreamReader(con.getInputStream()))

        /* Returns json body as one string line */
        val idClaims = reader.lines().findFirst().get()
            .replace(Regex("[{}\"]"), "") // remove brackets and citation signs
            .split(",") // split into claims-array
            .associate { claim -> // map claims to key-value
                val split = claim.split(":")
                split[0] to split[1]
            }

        try {
            val foundUser = userService.getUserBySub(jwt.claims["sub"] as String)

            userService.update(
                foundUser.copy(
                    name = idClaims["name"]!!,
                    email = idClaims["email"]!!
                )
            )

            return ResponseEntity.noContent().build()
        } catch (_: UserNotFoundException) { }

        val user = userService.add(
                User(idClaims["sub"]!!.removePrefix("auth0|"),
                        idClaims["name"]!!,
                        idClaims["email"]!!
                )
        )

        val uri = URI.create("api/v1/users/${user.uid}")

        return ResponseEntity.created(uri).build()
    }

    @GetMapping("api/v1/users")
    fun findAll() = ResponseEntity.ok(userService.findAll().map { it.toReadDto() })

    @GetMapping("api/v1/users/{id}")
    fun findById(@PathVariable(name = "id") uid: String): ResponseEntity<Any> {

        return try {
            return ResponseEntity.ok(userService.findById(uid).toReadDto())
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