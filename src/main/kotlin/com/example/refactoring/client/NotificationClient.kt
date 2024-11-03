package com.example.refactoring.client

import com.example.refactoring.model.ProductPriceChangeMessage
import com.example.refactoring.model.ProductStockUpdateMessage
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NotificationClient(private val webClient: WebClient.Builder) {

    suspend fun sendPriceChangeNotification(productId: String, oldPrice: Double, newPrice: Double): Boolean {
        val message = ProductPriceChangeMessage(productId, oldPrice, newPrice)

        return webClient.build()
            .post()
            .uri("http://localhost:9005/api/notifications/price-change")
            .bodyValue(message)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.NO_CONTENT) {
                    Mono.just(true)
                } else {
                    Mono.just(false)
                }
            }
            .awaitFirstOrNull() ?: false
    }

    suspend fun sendStockUpdateNotification(productId: String, stock: Int): Boolean {
        val message = ProductStockUpdateMessage(productId, stock)

        return webClient.build()
            .post()
            .uri("http://localhost:9005/api/notifications/stock-update")
            .bodyValue(message)
            .exchangeToMono { response ->
                if (response.statusCode() == HttpStatus.NO_CONTENT) {
                    Mono.just(true)
                } else {
                    Mono.just(false)
                }
            }
            .awaitFirstOrNull() ?: false
    }
}