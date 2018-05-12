package com.egco.mailbot.config

import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig {
    val firebaseServiceAccountUrl = "/Users/aussawin/Documents/mailbot/src/main/resources/service/mailbotmobile-firebase-adminsdk-mcecn-efc42e3d7d.json"
    val MESSAGE_PATH = "message"
    val GUIDE_PATH = "guide"
}