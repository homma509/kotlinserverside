package com.github.homma509.kotlinserverside

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinserversideApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<KotlinserversideApplication>(*args)
}
