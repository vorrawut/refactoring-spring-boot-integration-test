package com.example.refactoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories
class RefactoringApplication

fun main(args: Array<String>) {
	runApplication<RefactoringApplication>(*args)
}
