package com.hvz.repositories

import com.hvz.models.Mission
import org.springframework.data.jpa.repository.JpaRepository

interface MissionRepository : JpaRepository<Mission, Int>