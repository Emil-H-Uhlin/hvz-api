package com.hvz.services.chat

import com.hvz.exceptions.ChatMessageNotFoundException
import com.hvz.models.ChatMessage
import com.hvz.repositories.ChatRepository

class ChatServiceImpl(private val chatRepository: ChatRepository): ChatService {
    override fun findById(id: Int): ChatMessage = chatRepository.findById(id).orElseThrow { ChatMessageNotFoundException(id) }

    override fun findAll(): Collection<ChatMessage> = chatRepository.findAll()

    override fun add(entity: ChatMessage): ChatMessage = chatRepository.save(entity)

    override fun update(entity: ChatMessage) {
        chatRepository.save(entity)
    }

    override fun deleteById(id: Int) {
        chatRepository.deleteById(id)
    }
}