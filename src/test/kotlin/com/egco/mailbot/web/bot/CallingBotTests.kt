package com.egco.mailbot.web.bot

import com.egco.mailbot.domain.Log
import com.egco.mailbot.domain.User
import com.egco.mailbot.repository.LogRepository
import com.egco.mailbot.repository.UserRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import java.util.*
import kotlin.collections.ArrayList

@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@SpringBootTest
class CallingBotTests {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var logRepository: LogRepository

    @Autowired
    lateinit var userRepository: UserRepository


    @Before fun setup(){
        userRepository.deleteAll()
        logRepository.deleteAll()

        val user: ArrayList<User> = arrayListOf()
        (1..5).mapTo(user){
            User("user$it", "user$it@gmail.com", "password$it", "$it", it, "name$it", Date())
        }
        userRepository.save(user)
        val log: ArrayList<Log> = arrayListOf()
        (1..4).mapTo(log){
            Log("user$it", it, "user${it+1}", it+1, "subject$it", "note$it", "success", Date())
        }
        logRepository.save(log)
    }
}