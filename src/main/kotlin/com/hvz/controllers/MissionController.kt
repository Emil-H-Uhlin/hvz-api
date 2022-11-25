package com.hvz.controllers

import com.hvz.misc.GameState
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
                        private val gameService: GameService)