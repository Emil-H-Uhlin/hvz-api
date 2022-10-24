package com.hvz.services.user

import com.hvz.exceptions.UserNotFoundException
import com.hvz.models.User
import com.hvz.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository): UserService {
    companion object {
        private const val AUTH0_PREFIX = "auth0|"
    }
    override fun findById(id: String): User = userRepository.findById(id)
        .orElseThrow {
            UserNotFoundException(id)
        }

    override fun findAll(): Collection<User> = userRepository.findAll()

    override fun add(entity: User): User = userRepository.save(entity)

    override fun update(entity: User) {
        userRepository.save(entity)
    }

    override fun deleteById(id: String) {
        userRepository.deleteById(id)
    }

    override fun getUserBySub(sub: String): User = findById(sub.removePrefix(AUTH0_PREFIX))
}