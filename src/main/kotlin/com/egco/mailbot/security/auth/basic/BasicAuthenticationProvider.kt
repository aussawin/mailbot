package com.egco.mailbot.security.auth.basic

import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.UserRepository
import com.egco.mailbot.security.exception.UsernameNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.security.crypto.password.PasswordEncoder

@Component
class BasicAuthenticationProvider @Autowired
constructor(private val userRepository: UserRepository,
            private val passwordEncoder: PasswordEncoder): AuthenticationProvider{
    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal as String
        val password = authentication.credentials as String
        val user: User = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")

//        if (!passwordEncoder.matches(password, user.password)) {
//            throw BadCredentialsException("Authentication Failed. Password not valid.")
//        }

        if (password != user.password) {
            throw BadCredentialsException("Authentication Failed. Password not valid.")
        }

        return UsernamePasswordAuthenticationToken(user, null)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}