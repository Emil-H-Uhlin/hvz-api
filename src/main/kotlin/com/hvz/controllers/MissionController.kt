package com.hvz.controllers

import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.MissionNotFoundException
import com.hvz.misc.GameState
import com.hvz.models.Mission
import com.hvz.models.MissionAddDTO
import com.hvz.models.MissionEditDTO
import com.hvz.services.game.GameService
import com.hvz.services.mission.MissionService
import org.apache.coyote.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class MissionController(private val missionService: MissionService,
                        private val gameService: GameService) {

    //region Admin
    @GetMapping("missions")
    fun findAll() = ResponseEntity.ok(missionService.findAll().map { it.toReadDto() })

    @GetMapping("missions/{id}")
    fun findById(@PathVariable id: Int) = ResponseEntity.ok(missionService.findById(id).toReadDto())

    @PutMapping("missions/{id}")
    fun updateMission(@PathVariable id: Int,
                      @RequestBody dto: MissionEditDTO): ResponseEntity<Any> {
        if (dto.id != id) return ResponseEntity.badRequest().build()

        return try {
            val mission = missionService.findById(id)

            missionService.update(
                mission.copy(
                    name = dto.name,
                    description = dto.description,
                    lat = dto.lat,
                    lng = dto.lng
                )
            )

            ResponseEntity.noContent().build()
        } catch (missionNotFoundException: MissionNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("games/{game_id}/missions")
    fun addMission(@PathVariable(name = "game_id") gameId: Int,
                   @RequestBody dto: MissionAddDTO): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            if (game.gameState == GameState.COMPLETED)
                return ResponseEntity.badRequest().build()

            val addedMission = missionService.add(
                Mission(
                    dto.name,
                    dto.description,
                    dto.lat,
                    dto.lng
                )
            )

            val uri = URI.create("api/v1/missions/${addedMission.id}")

            ResponseEntity.created(uri).build()
        }
        catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("missions/{id}")
    fun deleteMission(@PathVariable id : Int): ResponseEntity<Any> {
        return try {
            missionService.findById(id)
            missionService.deleteById(id)

            ResponseEntity.noContent().build()
        } catch (missionNotFoundException: MissionNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }
    //endregion

    @GetMapping("games/{game_id}/missions")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {
        return try {
            val game = gameService.findById(gameId)

            ResponseEntity.ok(game.missions.map { it.toReadDto() })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}