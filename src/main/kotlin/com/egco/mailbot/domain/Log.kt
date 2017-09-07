package com.egco.mailbot.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "log")
data class Log (@field:NotNull var sender: String = "",
           @field:NotNull var target: String = "",
           @field:NotNull var subject: String = "",
           var note: String = "",
           var updatedAt: Date = Date()
           //@todo status require
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: Date = Date()
}