package com.egco.mailbot.dao

import java.util.*

data class LogTemplate(val sender: String = "",
                       val target: String = "",
                       val subject: String = "",
                       val note: String = "",
                       val date: Date = Date(),
                       val status: String = "")