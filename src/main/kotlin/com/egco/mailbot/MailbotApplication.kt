package com.egco.mailbot

import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.util.*

@SpringBootApplication
class MailbotApplication(val userRepository: UserRepository,
                         val logRepository: LogRepository){

    @Bean
    open fun init() = CommandLineRunner {

    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MailbotApplication::class.java, *args)
}
