package com.egco.mailbot.repository

import com.egco.mailbot.domain.BotPosition
import org.springframework.data.jpa.repository.JpaRepository

interface BotPositionRepository : JpaRepository<BotPosition, Long> {
    fun findByState(state: String): BotPosition
}