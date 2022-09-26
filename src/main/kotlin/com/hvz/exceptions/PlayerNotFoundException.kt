package com.hvz.exceptions

class PlayerNotFoundException(id: Int) : RuntimeException("There is no player with id $id")