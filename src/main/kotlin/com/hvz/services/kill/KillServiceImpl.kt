package com.hvz.services.kill

import com.hvz.exceptions.KillNotFoundException
import com.hvz.models.Kill
import com.hvz.repositories.KillRepository
import org.springframework.stereotype.Service

@Service
class KillServiceImpl(private val killRepository: KillRepository): KillService {
    override fun findById(id: Int): Kill = killRepository.findById(id)
        .orElseThrow {
            KillNotFoundException(id)
        }

    override fun findAll(): Collection<Kill> = killRepository.findAll()

    override fun add(entity: Kill): Kill = killRepository.save(entity)

    override fun update(entity: Kill) {
        killRepository.save(entity)
    }

    override fun deleteById(id: Int) {
        killRepository.deleteById(id)
    }
}