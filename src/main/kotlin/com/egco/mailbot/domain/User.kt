package com.egco.mailbot.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.validator.constraints.Email
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "users")
data class User(@field:NotNull var username: String = "",
                @field:NotNull @field:Email var email: String = "",
                @field:NotNull var password: String = "",
                @field:NotNull var pin: String = "",
                @field:NotNull var location: Int = 0,
                var name: String = "",
                var updatedAt: Date = Date()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: Date = Date()
}