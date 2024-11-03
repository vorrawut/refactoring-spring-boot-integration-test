package com.example.refactoring.web

import com.example.refactoring.mock.MockedProductNotificationServer
import com.example.refactoring.model.Product
import com.example.refactoring.repository.ProductRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import kotlin.test.Test

internal class PrivateShoppingIntegrationTest(
    @Autowired private val productRepository: ProductRepository,
) : BaseShoppingControllerTest() {
    private val mockedProductNotificationServer = MockedProductNotificationServer().apply { start() }

    private val baseUrl = "/api"
    private val productId = "MockProductID"

    @AfterEach
    fun afterEachTest() {
        mockedProductNotificationServer.close()
    }

    @BeforeEach
    fun setup(): Unit = runBlocking {
        productRepository.deleteAll().awaitFirstOrNull()
    }

    @Nested
    inner class ProductActionsTest {

        @Test
        fun `creating a product should trigger a stock update notification`(): Unit = runBlocking {
            mockedProductNotificationServer.enqueueJson(HttpStatus.NO_CONTENT)

            val newProduct = Product(id = productId, name = "Cool Sneakers", description = "Running shoes", price = 59.99, stock = 100)
            productRepository.save(newProduct)

            client.post().uri("$baseUrl/products")
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isCreated

            // Verify stock update notification was sent
            mockedProductNotificationServer.verifyStockUpdateNotification(productId, 100)
        }

        @Test
        fun `updating a product price should trigger a price change notification`() = runBlocking {
            mockedProductNotificationServer.enqueueJson(HttpStatus.NO_CONTENT)

            val updatedProduct = Product(id = productId, name = "Cool Sneakers", description = "Running shoes", price = 49.99, stock = 100)
            productRepository.save(updatedProduct)

            client.put().uri("$baseUrl/products/$productId")
                .bodyValue(updatedProduct)
                .exchange()
                .expectStatus().isOk

            // Verify price change notification was sent
            mockedProductNotificationServer.verifyPriceChangeNotification(productId, 20.0, 49.99)
        }

        @Test
        fun `deleting a product should stop any stock notifications`() = runBlocking {
            mockedProductNotificationServer.enqueueJson(HttpStatus.NO_CONTENT)

            val productToDelete = Product(id = productId, name = "Cool Sneakers", description = "Running shoes", price = 59.99, stock = 100)
            productRepository.save(productToDelete)

            client.delete().uri("$baseUrl/products/$productId")
                .exchange()
                .expectStatus().isNoContent

            // Verify that stock update notifications are stopped (this is a conceptual check)
            mockedProductNotificationServer.verifyStockUpdateNotification(productId, 0)
        }
    }
}