package com.example.refactoring.model

data class ProductStockUpdateMessage(
    val productId: String,
    val stock: Int
)