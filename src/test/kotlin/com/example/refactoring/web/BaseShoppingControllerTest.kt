package com.example.refactoring.web

import com.example.refactoring.SpringBootIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootIntegrationTest
abstract class BaseShoppingControllerTest {
    @Autowired
    protected lateinit var client: WebTestClient
}