package com.hvz.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["api/v1/players"])
class PlayerController {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Int): ResponseEntity<Nothing> = ResponseEntity.ok().build()
}