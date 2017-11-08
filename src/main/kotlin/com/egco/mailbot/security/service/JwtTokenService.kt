package com.egco.mailbot.security.service

import com.egco.mailbot.domain.User
import com.egco.mailbot.security.config.AuthProperties
import com.egco.mailbot.security.model.token.AccessJwtToken
import com.egco.mailbot.security.model.token.JwtToken
import com.egco.mailbot.security.model.token.TokenType
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.joda.time.DateTime
import org.springframework.stereotype.Service

@Service
class JwtTokenService(private val properties: AuthProperties) {


    fun createAccessJwtToken(user: User): JwtToken {
        val claims = Jwts.claims().setSubject(user.username)
        claims.put("type", TokenType.ACCESS_TOKEN)

        val currentTime = DateTime()
        val token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(properties.Issuer)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(Integer.parseInt(properties.tokenExpirationTime)).toDate())
                .signWith(SignatureAlgorithm.HS512, properties.signingKey)
                .compact()
        return AccessJwtToken(token, claims)
    }

    fun createRefreshToken(user: User): JwtToken {
        val claims = Jwts.claims().setSubject(user.username)
        claims.put("type", TokenType.REFRESH_TOKEN)

        val currentTime = DateTime()
        val token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(properties.Issuer)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(Integer.parseInt(properties.refreshTokenExpTime)).toDate())
                .signWith(SignatureAlgorithm.HS512, properties.signingKey)
                .compact()
        return AccessJwtToken(token, claims)
    }


}