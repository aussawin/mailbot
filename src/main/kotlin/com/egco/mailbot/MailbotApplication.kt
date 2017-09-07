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
        val userList: MutableList<User> = ArrayList()
        (1..5).mapTo(userList) { User("user$it", "user$it@gmail.com", "password", "1111", "user$it", Date()) }
        userRepository.save(userList.toList())
        val logList: MutableList<Log> = ArrayList()
        (1..3).mapTo(logList) { Log("user$it", "user${it+1}", "Subject#$it", "This is note #$it", Date()) }
        logRepository.save((logList.toList()))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MailbotApplication::class.java, *args)
}
