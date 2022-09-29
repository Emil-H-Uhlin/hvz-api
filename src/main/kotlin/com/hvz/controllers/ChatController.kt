package com.hvz.controllers

import com.hvz.exceptions.ChatMessageNotFoundException
import com.hvz.exceptions.GameNotFoundException
import com.hvz.exceptions.PlayerNotFoundException
import com.hvz.models.ChatMessage
import com.hvz.models.ChatMessageAddDTO
import com.hvz.models.ChatMessageEditDTO
import com.hvz.services.chat.ChatService
import com.hvz.services.game.GameService
import com.hvz.services.player.PlayerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(path = ["api/v1/"])
@CrossOrigin(origins = ["*"])
class ChatController(private val chatService: ChatService,
                     private val gameService: GameService,
                     private val playerService: PlayerService) {

    //region Admin
    @GetMapping("chat")
    fun findAll() = ResponseEntity.ok(chatService.findAll().map { it.toReadDto() })

    @GetMapping("chat/{id}")
    fun findById(@PathVariable id: Int) = ResponseEntity.ok(chatService.findById(id).toReadDto())

    @PutMapping("chat/{id}")
    fun updateChatMessage(@PathVariable id: Int,
                          @RequestBody dto: ChatMessageEditDTO): ResponseEntity<Any> {
        if (dto.id != id) return ResponseEntity.badRequest().build()

        return try {
            val chatMessage = chatService.findById(id)

            chatService.update(
                chatMessage.copy(
                    message = dto.message,
                    zombieGlobal = dto.zombieGlobal,
                    humanGlobal = dto.humanGlobal
                )
            )

            ResponseEntity.noContent().build()
        } catch (chatMessageNotFoundException: ChatMessageNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("chat/{id}")
    fun deleteMessage(@PathVariable id: Int): ResponseEntity<Any> {

        return try {
            chatService.findById(id)
            chatService.deleteById(id)

            ResponseEntity.noContent().build()
        } catch (chatMessageNotFoundException: ChatMessageNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }
    //endregion

    @GetMapping("games/{game_id}/chat")
    fun findAll(@PathVariable(name = "game_id") gameId: Int): ResponseEntity<Any> {

        return try {
            val game = gameService.findById(gameId)

            // TODO: get correct chat depending on player human status

            ResponseEntity.ok(game.messages.map { it.id })
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("games/{game_id}/chat")
    fun addChatMessage(@PathVariable(name = "game_id") id: Int,
                       @RequestBody dto: ChatMessageAddDTO) : ResponseEntity<Any> {

        return try {
            val game = gameService.findById(id)
            val sender = playerService.findById(dto.senderId)

            val addedChatMessage = chatService.add(
                ChatMessage(
                    message = dto.message,
                    zombieGlobal = dto.zombieGlobal,
                    humanGlobal = dto.humanGlobal,
                    sender = sender,
                    game = game
                )
            )

            val uri = URI.create("api/v1/chat/${addedChatMessage.id}")

            ResponseEntity.created(uri).build()
        } catch (gameNotFoundException: GameNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (playerNotFoundException: PlayerNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}