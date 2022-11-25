package com.hvz.services.mission

import com.hvz.models.Mission
import com.hvz.repositories.MissionRepository
import org.springframework.stereotype.Service

@Service
class MissionServiceImpl(private val missionRepository: MissionRepository): MissionService {
    override fun findById(id: Int): Mission? = missionRepository.findById(id).let {
        return if (it.isPresent) it.get() else null
    }

    override fun findAll(): Collection<Mission> = missionRepository.findAll()

    override fun add(entity: Mission): Mission = missionRepository.save(entity)

    override fun update(entity: Mission) {
        missionRepository.save(entity)
    }

    override fun deleteById(id: Int) {
        missionRepository.deleteById(id)
    }
}