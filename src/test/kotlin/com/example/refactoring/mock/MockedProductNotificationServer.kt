package com.example.refactoring.mock

import com.example.refactoring.model.ProductPriceChangeMessage
import com.example.refactoring.model.ProductStockUpdateMessage
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpMethod

class MockedProductNotificationServer : TestMockedWebServer(9005) {

    fun verifyPriceChangeNotification(productId: String, oldPrice: Double, newPrice: Double) {
        val actualRequest = webServer.takeRequest()
        assertThat(actualRequest.method).isEqualTo(HttpMethod.POST.name())
        assertThat(actualRequest.path).isEqualTo("/api/notifications/price-change")
        with(actualRequest.readBody<ProductPriceChangeMessage>()) {
            assertThat(this.productId).isEqualTo(productId)
            assertThat(this.oldPrice).isEqualTo(oldPrice)
            assertThat(this.newPrice).isEqualTo(newPrice)
        }
    }

    fun verifyStockUpdateNotification(productId: String, stock: Int) {
        val actualRequest = webServer.takeRequest()
        assertThat(actualRequest.method).isEqualTo(HttpMethod.POST.name())
        assertThat(actualRequest.path).isEqualTo("/api/notifications/stock-update")
        with(actualRequest.readBody<ProductStockUpdateMessage>()) {
            assertThat(this.productId).isEqualTo(productId)
            assertThat(this.stock).isEqualTo(stock)
        }
    }
}