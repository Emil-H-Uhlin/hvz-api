package com.hvz.controllers

import com.hvz.misc.GameState
import com.hvz.models.Kill
import com.hvz.models.KillAddDTO
import com.hvz.models.KillEditDTO
import com.hvz.models.Player
import com.hvz.services.game.GameService
import com.hvz.services.kill.KillService
import com.hvz.services.player.PlayerService
import com.hvz.services.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class KillController(val killService: KillService,
                     val gameService: GameService,
                     val playerService: PlayerService,
                     val userService: UserService,
) {
    @GetMapping("kills")
    fun findAll() = ResponseEntity.ok(killService.findAll().map { it.toReadDto() })

    @GetMapping("kills/{id}")
    fun findById(@PathVariable id: Int): ResponseEntity<Any> =
        when (val kill = killService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(kill.toReadDto())
        }

    @PutMapping("kills/{id}")
    fun updateKill(@PathVariable id: Int,
                   @RequestBody dto: KillEditDTO): ResponseEntity<Any> {

        if (dto.id != id)
            return ResponseEntity.badRequest().build()

        return when (val kill = killService.findById(id)) {
            null -> ResponseEntity.notFound().build()
            else -> {
                killService.update(
                    kill.copy(
                        story = dto.story,
                        lat = dto.lat,
                        lng = dto.lng
                    )
                )

                ResponseEntity.noContent().build()
            }
        }
    }

    @GetMapping("games/{game_id}/kills")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> =
        when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(game.kills.map { it.toReadDto() })
        }

    @PostMapping("kills")
    fun addKill(@RequestBody dto: KillAddDTO,
                @AuthenticationPrincipal jwt: Jwt): ResponseEntity<Any> {

        return try {
            val victim: Player

            UUID.fromString(dto.victimBiteCode)

            (playerService.findByBiteCode(dto.victimBiteCode) ?: return ResponseEntity.notFound().build()).apply {
                victim = copy(human = !human, patientZero = patientZero)
            }

            val game = victim.game!!

            if (game.gameState != GameState.PLAYING)
                return ResponseEntity.badRequest().build()

            val killer = userService.getUserBySub(jwt.claims["sub"] as String)!!
                .players.find { player -> player.game!!.id == game.id }
                ?: return ResponseEntity.badRequest().build()

            // killer is human or victim was already dead
            if (killer.human || victim.human)
                return ResponseEntity.badRequest().build()

            playerService.update(victim)

            val addedKill = killService.add(
                Kill(
                    dto.story,
                    dto.lat,
                    dto.lng,
                    killer = killer,
                    victim = victim,
                    game = game
                )
            )

            val uri = URI.create("api/v1/kills/${addedKill.id}")

            ResponseEntity.created(uri).build()
        }
        catch (illegalArgumentException: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}