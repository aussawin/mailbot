package com.egco.mailbot.security.auth.jwt.extractor

interface TokenExtractor {
    fun extract(payload: String): String
}