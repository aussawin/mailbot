package com.egco.mailbot.service

import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import java.util.concurrent.CompletableFuture
import org.json.XMLTokener.entity
import java.util.ArrayList
import org.springframework.http.client.ClientHttpRequestInterceptor



class AndroidPushNotificationsService {
    private val FIREBASE_SERVER_KEY = "AAAAuXc64k0:APA91bER6pz-2KSzpc-wKfQXDmmWhwEGK6iBr-XN-W3BvW1jjbLsJXsfJBzC1CZ7xzC5wb7ya30PgmXHdFg6p8Jr0HrAzQilrk94uwE9mqjR6X0Gi0iUfio2pKjGfHKag7jpEB8c4NVT"
    private val FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send"

    fun send(entity: HttpEntity<String>): CompletableFuture<String>{
        val restTemplate = RestTemplate()
        val interceptors = ArrayList<ClientHttpRequestInterceptor>()
        interceptors.add(HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY))
        interceptors.add(HeaderRequestInterceptor("Content-Type", "application/json"))
        restTemplate.interceptors = interceptors

        val firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String::class.java)

        return CompletableFuture.completedFuture(firebaseResponse)
    }
}