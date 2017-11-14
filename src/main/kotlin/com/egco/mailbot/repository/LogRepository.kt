package com.egco.mailbot.repository

import com.egco.mailbot.domain.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository : JpaRepository<Log, Long> {
    fun fingByStatus(status: String): Log?
    fun findBySenderOrderByCreatedAt(sender: String): ArrayList<Log>?
    fun findByStatusOrderByCreatedAt(status: String): ArrayList<Log>?
}