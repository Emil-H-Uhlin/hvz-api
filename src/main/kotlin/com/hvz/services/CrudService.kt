package com.hvz.services

interface CrudService <T, ID> {
    fun findById(id: ID): T?
    fun findAll(): Collection<T>
    fun add(entity: T): T
    fun update(entity: T)
    fun deleteById(id: ID)
}