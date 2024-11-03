package com.example.refactoring.mock

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

abstract class TestMockedWebServer(private val port: Int) {
    protected val webServer = MockWebServer()

    fun start() {
        webServer.start(port)
    }

    fun close() {
        webServer.close()
    }

    fun enqueueJson(status: HttpStatus, payload: Any? = null, headers: Map<String, String>? = null) {
        val response = MockResponse().setResponseCode(status.value())
        if (payload != null) {
            val body = when (payload) {
                is String -> payload
                else -> objectMapper.writeValueAsString(payload)
            }
            response.setBody(body).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
        headers?.forEach { response.addHeader(it.key, it.value) }
        webServer.enqueue(response)
    }

    fun takeRequest(): RecordedRequest = webServer.takeRequest()

    fun verifyRequest(httpMethod: HttpMethod, path: String) {
        val request = takeRequest()
        assertThat(request.method).isEqualTo(httpMethod.name())
        assertThat(request.path).isEqualTo(path)
    }
}