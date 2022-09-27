package com.hvz.repositories

import com.hvz.models.Kill
import org.springframework.data.jpa.repository.JpaRepository

interface KillRepository: JpaRepository<Kill, Int>