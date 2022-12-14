package com.hvz.services.chat

import com.hvz.models.ChatMessage
import com.hvz.repositories.ChatRepository
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(private val chatRepository: ChatRepository): ChatService {
    override fun findById(id: Int): ChatMessage? = chatRepository.findById(id).let {
        return if (it.isPresent) it.get() else null
    }

    override fun findAll(): Collection<ChatMessage> = chatRepository.findAll()

    override fun add(entity: ChatMessage): ChatMessage = chatRepository.save(entity)

    override fun update(entity: ChatMessage) {
        chatRepository.save(entity)
    }

    override fun deleteById(id: Int) {
        chatRepository.deleteById(id)
    }
}