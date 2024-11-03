package com.example.refactoring.repository


import com.example.refactoring.model.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProductRepository : ReactiveMongoRepository<Product, String> {
}