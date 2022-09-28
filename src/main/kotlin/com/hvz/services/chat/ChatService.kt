package com.hvz.services.chat

import com.hvz.models.ChatMessage
import com.hvz.services.CrudService

interface ChatService: CrudService<ChatMessage, Int>