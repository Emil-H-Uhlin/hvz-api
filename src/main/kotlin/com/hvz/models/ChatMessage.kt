package com.hvz.models

import java.sql.Timestamp
import java.time.Instant
import javax.persistence.*

@Entity
data class ChatMessage(
    @Column(name = "message")
    val message: String,

    @Column(name = "is_zombie_global")
    val zombieGlobal: Boolean,

    @Column(name = "is_human_global")
    val humanGlobal: Boolean,

    @Column(name = "time_sent")
    val messageTime: Timestamp = Timestamp.from(Instant.now()),

    @ManyToOne
    @JoinColumn(name = "sender_id")
    val sender: Player? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = -1,
) {
    fun toReadDto() = ChatMessageReadDTO(id, message,
        zombieGlobal,
        humanGlobal,
        messageTime,
        sender?.id ?: -1
    )
}

data class ChatMessageReadDTO(val id: Int, val message: String,
                              val zombieGlobal: Boolean,
                              val humanGlobal: Boolean,
                              val messageTime: Timestamp,
                              val sender: Int,
)

data class ChatMessageEditDTO(val id: Int, val message: String,
                              val zombieGlobal: Boolean,
                              val humanGlobal: Boolean,
)

data class ChatMessageAddDTO(val message: String,
                             val zombieGlobal: Boolean,
                             val humanGlobal: Boolean,
                             val senderId: Int
)