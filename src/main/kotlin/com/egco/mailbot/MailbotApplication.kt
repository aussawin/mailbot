package com.egco.mailbot

import com.egco.mailbot.config.FirebaseConfig
import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.io.FileInputStream
import java.util.*

@SpringBootApplication
class MailbotApplication(val firebaseConfig: FirebaseConfig){

    @Bean
    open fun init() = CommandLineRunner {

        val serviceAccount = FileInputStream(firebaseConfig.firebaseServiceAccountUrl)

        val options = FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://mailbotmobile.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)

    }
}

fun main(args: Array<String>) {
    SpringApplication.run(MailbotApplication::class.java, *args)
}
