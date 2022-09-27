package com.hvz.exceptions

class KillNotFoundException(id: Int): RuntimeException("No kill found with id $id")