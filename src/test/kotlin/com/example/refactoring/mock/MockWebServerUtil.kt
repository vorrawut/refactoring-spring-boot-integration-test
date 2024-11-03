package com.example.refactoring.mock

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.mockwebserver.RecordedRequest

val objectMapper =
    ObjectMapper().apply {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        registerModule(KotlinModule.Builder().build())
        registerModule(Jdk8Module())
        registerModule(JavaTimeModule())
    }

inline fun <reified T : Any> RecordedRequest.readBody(): T = objectMapper.readValue(this.body.readUtf8())