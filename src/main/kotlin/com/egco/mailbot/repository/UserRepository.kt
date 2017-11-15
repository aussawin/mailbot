package com.egco.mailbot.repository

import com.egco.mailbot.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByName(name: String): User?
    fun existsByName(name: String): Boolean
}