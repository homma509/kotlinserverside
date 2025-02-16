package com.github.homma509.kotlinserverside.util

/**
 * ドメインオブジェクトのバリデーションにおけるエラー型
 *
 * 必ずエラーメッセージを記述する
 *
 */
interface ValidationError {
    /**
     * エラーメッセージ
     */
    val message: String
}
