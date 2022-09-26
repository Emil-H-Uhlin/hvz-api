package com.hvz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HvzApiApplication

fun main(args: Array<String>) {
    runApplication<HvzApiApplication>(*args)
}
