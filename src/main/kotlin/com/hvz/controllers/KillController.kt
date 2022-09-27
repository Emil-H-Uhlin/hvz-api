package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.KillNotFoundException
import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.misc.GameState
import com.hvz.models.Kill
import com.hvz.models.KillAddDTO
import com.hvz.models.KillEditDTO
import com.hvz.models.Player
import com.hvz.services.game.GameService
import com.hvz.services.kill.KillService
import com.hvz.services.player.PlayerService
import org.springframework.http.ResponseEntity
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
class KillController(val killService: KillService,
                     val gameService: GameService,
                     val playerService: PlayerService,
) {

    @GetMapping("kills")
    fun findAll() = ResponseEntity.ok(killService.findAll().map { it.toReadDto() })

    @GetMapping("games/{game_id}/kills")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {
        return try {
            val game = gameService.findById(gameId)
            ResponseEntity.ok(game.kills.map { it.toReadDto() })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("kills/{id}")
    fun findById(@PathVariable id: Int) = ResponseEntity.ok(killService.findById(id).toReadDto())

    @PutMapping("kills/{id}")
    fun updateKill(@PathVariable id: Int,
                   @RequestBody dto: KillEditDTO): ResponseEntity<Any> {
        if (dto.id != id) return ResponseEntity.badRequest().build()

        return try {
            val kill = killService.findById(id)

            killService.update(
                kill.copy(
                    story = dto.story,
                    lat = dto.lat,
                    lng = dto.lng
                )
            )

            ResponseEntity.noContent().build()
        } catch (killNotFoundException: KillNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("games/{game_id}/kills")
    fun addKill(@PathVariable(name = "game_id") gameId: Int,
                @RequestBody dto: KillAddDTO): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            if (game.gameState != GameState.PLAYING)
                return ResponseEntity.badRequest().build()

            val killer = playerService.findById(dto.killerId)

            val victim: Player

            playerService.findByBiteCode(dto.victimBiteCode).apply {
                victim = copy(human = !this.human)
            }

            // killer is human or victim was already dead
            if (killer.human || victim.human)
                ResponseEntity.badRequest().build<Nothing>()

            playerService.update(victim)

            val addedKill = killService.add(
                Kill(
                    dto.story,
                    dto.lat,
                    dto.lng
                ).apply {
                    this.killer = killer
                    this.victim = victim
                    this.game = game
                }
            )

            val uri = URI.create("api/v1/kills/${addedKill.id}")

            ResponseEntity.created(uri).build()
        }
        catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
        catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }


}