package com.hvz.exceptions

class GameNotFoundException(id: Int) : RuntimeException("No game found with id $id")