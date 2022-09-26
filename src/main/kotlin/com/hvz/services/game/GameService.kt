package com.hvz.services.game

import com.hvz.models.Game
import com.hvz.services.CrudService

interface GameService: CrudService <Game, Int>