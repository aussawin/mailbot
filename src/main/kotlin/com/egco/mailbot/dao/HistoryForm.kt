package com.egco.mailbot.dao

import java.util.*

data class HistoryForm(val sender: String,
                       val target: String,
                       val subject: String,
                       val note: String,
                       val date: Date = Date()
                  //@todo send status require
) {
}