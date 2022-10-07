package com.hvz.repositories

import com.hvz.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String>