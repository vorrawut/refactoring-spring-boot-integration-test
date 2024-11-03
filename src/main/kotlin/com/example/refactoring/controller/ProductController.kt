package com.example.refactoring.controller

import com.example.refactoring.model.Product
import com.example.refactoring.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    /**
     * Create a new product.
     * Sends a stock update notification upon successful creation.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createProduct(@RequestBody product: Product): Product {
        return productService.createProduct(product)
    }

    /**
     * Update an existing product.
     * Sends notifications for price changes or stock updates if applicable.
     */
    @PutMapping("/{id}")
    suspend fun updateProduct(
        @PathVariable id: String,
        @RequestBody updatedProduct: Product
    ): Product? {
        return productService.updateProduct(id, updatedProduct)
    }

    /**
     * Get a product by its ID.
     */
    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: String): Product? {
        return productService.getProductById(id)
    }

    /**
     * Delete a product by its ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProduct(@PathVariable id: String): Boolean {
        return productService.deleteProduct(id)
    }
}