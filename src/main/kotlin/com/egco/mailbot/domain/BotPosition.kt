package com.egco.mailbot.domain

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "bot_position")
data class BotPosition(@field:NotNull var position: Int = 0,
                       @field:NotNull var status: String = ""){
    @Id
    val state: String = ""
}