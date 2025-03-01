package com.github.homma509.kotlinserverside.presentation

import arrow.core.getOrElse
import com.github.homma509.kotlinserverside.presentation.model.Article
import com.github.homma509.kotlinserverside.presentation.model.SingleArticleResponse
import com.github.homma509.kotlinserverside.usecase.ShowArticleUseCase
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * 作成済記事記事のコントローラー
 *
 * @property showArticleUseCase 単一記事取得ユースケース
 */
@RestController
@Validated
class ArticleController(val showArticleUseCase: ShowArticleUseCase) {
    /**
     * 単一の作成済記事取得
     *
     * @return
     */
    @GetMapping("/api/articles/{slug}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getArticle(
        @Parameter(description = "記事の slug", required = true, schema = Schema(minLength = 32, maxLength = 32)) @Valid @PathVariable("slug") @Length(min = 32, max = 32) slug: String,
    ): ResponseEntity<SingleArticleResponse> {
        /**
         * 作成済記事の取得
         */
        val createdArticle = showArticleUseCase.execute(slug).getOrElse { TODO() } // エラーハンドリングは後で実装する

        return ResponseEntity(
            SingleArticleResponse(
                article = Article(
                    slug = createdArticle.slug.value,
                    title = createdArticle.title.value,
                    description = createdArticle.description.value,
                    body = createdArticle.body.value,
                )
            ),
            HttpStatus.OK
        )
    }
}
