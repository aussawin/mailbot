package com.egco.mailbot.security.model.token

import com.fasterxml.jackson.annotation.JsonIgnore
import io.jsonwebtoken.Claims

class AccessJwtToken(override val token: String, @JsonIgnore
val claims: Claims) : JwtToken