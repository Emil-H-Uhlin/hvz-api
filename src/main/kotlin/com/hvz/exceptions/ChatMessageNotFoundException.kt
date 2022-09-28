package com.hvz.exceptions

class ChatMessageNotFoundException(id: Int): RuntimeException("No message found with id $id")