package com.hvz.exceptions

class PlayerNotFoundException : RuntimeException {
    constructor(id: Int): super("No player found with id $id")
    constructor(biteCode: String): super("No player found with supplied bite code '$biteCode'")
}