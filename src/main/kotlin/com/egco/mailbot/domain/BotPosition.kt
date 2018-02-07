package com.egco.mailbot.domain

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "bot_position")
data class BotPosition(@field:NotNull var state: String = "",
                       @field:NotNull var position: Int = 0 ){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}