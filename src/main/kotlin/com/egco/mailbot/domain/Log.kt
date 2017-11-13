package com.egco.mailbot.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "log")
data class Log (@field:NotNull var sender: String = "",
                @field:NotNull var senderLocation: Int = 0,
                @field:NotNull var target: String = "",
                @field:NotNull var targetLocation: Int = 0,
                @field:NotNull var subject: String = "",
                var note: String = "",
                var status: String = "",
                @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
                var updatedAt: Date = Date()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
    val createdAt: Date = Date()
}