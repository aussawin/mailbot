package com.egco.mailbot.dao

data class CallingReq(val target: String = "",
                      val subject: String = "",
                      val note: String = "") {
}