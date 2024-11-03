package com.example.refactoring.model

data class ProductPriceChangeMessage(
    val productId: String,
    val oldPrice: Double,
    val newPrice: Double
)