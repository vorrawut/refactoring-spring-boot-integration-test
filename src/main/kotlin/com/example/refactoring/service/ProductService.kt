package com.example.refactoring.service

import com.example.refactoring.client.NotificationClient
import com.example.refactoring.model.Product
import com.example.refactoring.repository.ProductRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val notificationClient: NotificationClient,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun createProduct(product: Product): Product {
        val savedProduct = productRepository.save(product).awaitSingle()

        try {
            // Notify the external service about stock update after product creation
            notificationClient.sendStockUpdateNotification(savedProduct.id!!, savedProduct.stock)
        } catch (e: Exception) {
            logger.error("createProduct: Notification sendStockUpdateNotification failed to send with productId ${savedProduct.id} and stock ${savedProduct.stock}:")
        }

        return savedProduct
    }

    suspend fun updateProduct(id: String, updatedProduct: Product): Product? {
        val existingProduct = productRepository.findById(id).awaitFirstOrNull() ?: return null

        val newProduct = existingProduct.copy(
            name = updatedProduct.name,
            description = updatedProduct.description,
            price = updatedProduct.price,
            stock = updatedProduct.stock
        )

        val savedProduct = productRepository.save(newProduct).awaitSingle()

        try {
            // Notify external API if the price has changed
            if (existingProduct.price != updatedProduct.price) {
                notificationClient.sendPriceChangeNotification(
                    savedProduct.id!!,
                    existingProduct.price,
                    savedProduct.price
                )
            }

            // Notify external API if the stock has changed
            if (existingProduct.stock != updatedProduct.stock) {
                notificationClient.sendStockUpdateNotification(savedProduct.id!!, savedProduct.stock)
            }
        } catch (e: Exception) {
            logger.error("updateProduct: Notification sendStockUpdateNotification failed to send with productId ${savedProduct.id} and stock ${savedProduct.stock}:")
        }

        return savedProduct
    }

    suspend fun getProductById(id: String): Product? {
        return productRepository.findById(id).awaitFirstOrNull()
    }

    suspend fun deleteProduct(id: String): Boolean {
        return productRepository.findById(id).awaitFirstOrNull()?.let {
            productRepository.delete(it).awaitSingle()
            true
        } ?: false
    }
}