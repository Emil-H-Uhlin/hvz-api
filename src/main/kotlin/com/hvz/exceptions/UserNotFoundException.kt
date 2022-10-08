package com.hvz.exceptions

class UserNotFoundException(uid: String): RuntimeException("No user registered with uid '$uid'")