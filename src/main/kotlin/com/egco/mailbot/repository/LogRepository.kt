package com.egco.mailbot.repository

import com.egco.mailbot.domain.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository : JpaRepository<Log, Long> {
    fun findBySenderContainingOrderByCreatedAt(sender: String): ArrayList<Log>?
}