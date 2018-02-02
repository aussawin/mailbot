package com.egco.mailbot.controller

import com.egco.mailbot.config.FirebaseConfig
import com.egco.mailbot.dao.Post
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream

class FirebaseController{

    fun send(message: Post) {

        val ref = FirebaseDatabase.getInstance().reference
        val pushRef = ref.child("message")
        pushRef.setValueAsync(message)
    }
}