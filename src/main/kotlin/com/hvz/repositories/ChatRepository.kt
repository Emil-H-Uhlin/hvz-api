package com.hvz.repositories

import com.hvz.models.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<ChatMessage, Int>