package com.hvz.controllers

import com.hvz.models.Mission
import com.hvz.models.MissionAddDTO
import com.hvz.models.MissionEditDTO
import com.hvz.services.game.GameService
import com.hvz.services.mission.MissionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class MissionController(private val missionService: MissionService,
                        private val gameService: GameService) {

    @GetMapping("games/{game_id}/missions")
    fun getMissionsInGame(@PathVariable(name = "game_id") gameId: Int) = ResponseEntity.ok(
        missionService.findAll().map {
            it.toReadDto()
        }
    )

    @GetMapping("games/{game_id}/missions/{mission_id}")
    fun getMissionInGame(@PathVariable(name = "game_id") gameId: Int,
                         @PathVariable(name = "mission_id") missionId: Int) =
        when (gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> when (val mission = missionService.findById(missionId)) {
                null -> ResponseEntity.notFound().build()
                else -> ResponseEntity.ok(mission.toReadDto())
            }
        }

    @PutMapping("games/{game_id}/missions/{mission_id}")
    fun updateMissionInGame(@PathVariable(name = "game_id") gameId: Int,
                            @PathVariable(name = "mission_id") missionId: Int,
                            @RequestBody dto: MissionEditDTO): ResponseEntity<Any> {

        if (dto.id != missionId)
            return ResponseEntity.badRequest().build()

        return when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> when (val mission = missionService.findById(missionId)) {
                null -> ResponseEntity.notFound().build()
                else -> {
                    if (mission.game!!.id != game.id)
                        return ResponseEntity.badRequest().build()

                    missionService.update(mission.copy(
                        name = dto.name,
                        description = dto.description,
                        lat = dto.lat,
                        lng = dto.lng
                    ))

                    return ResponseEntity.noContent().build()
                }
            }
        }
    }

    @PostMapping("games/{game_id}/missions")
    fun addMissionToGame(@PathVariable(name = "game_id") gameId: Int,
                         @RequestBody dto: MissionAddDTO): ResponseEntity<Any> =
        when (val game = gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> with (missionService.add(Mission(
                dto.name,
                dto.description,
                dto.lat,
                dto.lng,
                game = game
            ))) {
                val uri = URI.create("api/v1/games/${gameId}/missions/${id}")

                return ResponseEntity.created(uri).build()
            }
        }

    @DeleteMapping("games/{game_id}/missions/{mission_id}")
    fun deleteMissionFromGame(@PathVariable(name = "game_id") gameId: Int,
                              @PathVariable(name = "mission_id") missionId: Int): ResponseEntity<Any> =
        when (gameService.findById(gameId)) {
            null -> ResponseEntity.notFound().build()
            else -> when (missionService.findById(missionId)) {
                null -> ResponseEntity.notFound().build()
                else -> {
                    missionService.deleteById(missionId)

                    return ResponseEntity.noContent().build()
                }
            }
        }

}