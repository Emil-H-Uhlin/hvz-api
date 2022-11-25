package com.hvz.controllers

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
    fun deleteById(@PathVariable(name = "id") uid: String): ResponseEntity<Any> =
        when (userService.findById(uid)) {
            null -> ResponseEntity.badRequest().build()
            else -> {
                userService.deleteById(uid)
                ResponseEntity.noContent().build()
            }
        }

    @PutMapping("/register")
    fun registerUser(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

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

        return when (val foundUser = userService.getUserBySub(jwt.claims["sub"] as String)) {
            null -> {
                val user = userService.add(
                    User(idClaims["sub"]!!.removePrefix("auth0|"),
                        idClaims["name"]!!,
                        idClaims["email"]!!
                    )
                )

                val uri = URI.create("api/v1/users/${user.uid}")

                ResponseEntity.created(uri).body(user.toReadDto())
            }
            else -> {
                val updatedUser = foundUser.copy(name = idClaims["name"]!!, email = idClaims["email"]!!)
                userService.update(updatedUser)

                ResponseEntity.ok(updatedUser.toReadDto())
            }
        }
    }

    @GetMapping("api/v1/users")
    fun findAll() = ResponseEntity.ok(userService.findAll().map { it.toReadDto() })

    @GetMapping("api/v1/users/{id}")
    fun findById(@PathVariable(name = "id") uid: String): ResponseEntity<Any> =
        when (val user = userService.findById(uid)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(user.toReadDto())
        }

    @GetMapping("api/v1/games/{game_id}/users")
    fun findByGameId(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> =
        when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(
                userService.findAll().filter { user ->
                    game.players.find { it.user!!.uid == user.uid } != null
                }.map {
                    it.toReadDto()
                }
            )
        }
}