package com.github.homma509.kotlinserverside

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * ImplementingServerSideKotlinDevelopmentApplication
 *
 */
@SpringBootApplication
class KotlinserversideApplication

/**
 * main
 *
 * サンプルアプリケーションのメイン関数
 *
 * @param args
 */fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<KotlinserversideApplication>(*args)
}
