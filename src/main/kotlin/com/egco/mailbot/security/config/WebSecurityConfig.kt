package com.egco.mailbot.security.config

import com.egco.mailbot.security.RestAuthenticationEntryPoint
import com.egco.mailbot.security.auth.basic.BasicAuthenticationProvider
import com.egco.mailbot.security.auth.basic.BasicLoginProcessingFilter
import com.egco.mailbot.security.auth.jwt.JwtAuthenticationProvider
import com.egco.mailbot.security.auth.jwt.JwtTokenAuthenticationProcessingFilter
import com.egco.mailbot.security.auth.jwt.SkipPathRequestMatcher
import com.egco.mailbot.security.auth.jwt.extractor.TokenExtractor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig(private val successHandler: AuthenticationSuccessHandler,
                        private val failureHandler: AuthenticationFailureHandler,
                        private val authenticationEntryPoint: RestAuthenticationEntryPoint,
                        private val tokenExtractor: TokenExtractor,
                        private val basicAuthenticationProvider: BasicAuthenticationProvider,
                        private val jwtAuthenticationProvider: JwtAuthenticationProvider,
                        private val objectMapper: ObjectMapper) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(web: WebSecurity?) {
        web!!.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.headers().cacheControl()
        http.authorizeRequests()
                .antMatchers(FORM_BASED_LOGIN_ENTRY_POINT).permitAll()
                .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll()
                .antMatchers(REGISTER_ENTRY_POINT).permitAll()
                .antMatchers(FORM_BASED_BOT_CONTROLLER_ENTRY_POINT).permitAll()
                .antMatchers(API_ENTRY_POINT).authenticated()
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        http.addFilterBefore(buildBasicLoginProcessingFilter(),
                UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(),
                UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(basicAuthenticationProvider)
        auth?.authenticationProvider(jwtAuthenticationProvider)
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    @Throws(Exception::class)
    protected fun buildBasicLoginProcessingFilter(): BasicLoginProcessingFilter {
        val filter = BasicLoginProcessingFilter(FORM_BASED_LOGIN_ENTRY_POINT, successHandler, failureHandler, objectMapper)
        filter.setAuthenticationManager(authenticationManagerBean())
        return filter
    }

    @Bean
    @Throws(Exception::class)
    protected fun buildJwtTokenAuthenticationProcessingFilter(): JwtTokenAuthenticationProcessingFilter {
        val pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_BOT_CONTROLLER_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT, REGISTER_ENTRY_POINT)
        val matcher = SkipPathRequestMatcher(pathsToSkip, API_ENTRY_POINT)
        val filter = JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher)
        filter.setAuthenticationManager(authenticationManagerBean())
        return filter
    }

    companion object {
        // Header
        val JWT_TOKEN_HEADER_PARAM = "X-Authorization"
        const val JWT_REFRESH_TOKEN_HEADER_PARAM = "X-Refresh-Token"
        // End points
        private val FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login"
        private val FORM_BASED_BOT_CONTROLLER_ENTRY_POINT = "/api/botController/**"
        private val TOKEN_REFRESH_ENTRY_POINT = "/api/auth/refreshToken"
        private val API_ENTRY_POINT = "/api/**"
        private val REGISTER_ENTRY_POINT = "/api/auth/register"
    }
}