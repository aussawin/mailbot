package com.egco.mailbot.dao

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RequestLogin @JsonCreator
constructor(@param:JsonProperty("username") val username: String,
            @param:JsonProperty("password") val password: String)