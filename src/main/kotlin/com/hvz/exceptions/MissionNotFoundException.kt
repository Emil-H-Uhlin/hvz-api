package com.hvz.exceptions

class MissionNotFoundException(id: Int) : RuntimeException("No mission found with id $id")