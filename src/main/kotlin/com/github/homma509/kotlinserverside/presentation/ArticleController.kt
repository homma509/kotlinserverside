package com.github.homma509.kotlinserverside.presentation

import arrow.core.getOrElse
import com.github.homma509.kotlinserverside.presentation.model.Article
import com.github.homma509.kotlinserverside.presentation.model.GenericErrorModel
import com.github.homma509.kotlinserverside.presentation.model.GenericErrorModelErrors
import com.github.homma509.kotlinserverside.presentation.model.SingleArticleResponse
import com.github.homma509.kotlinserverside.usecase.ShowArticleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
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
    @Operation(
        summary = "単一記事取得",
        operationId = "getArticle",
        description = "slug に一致する記事を取得します。",
        tags = ["articles"],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        schema = Schema(implementation = SingleArticleResponse::class),
                        examples = [
                            ExampleObject(
                                name = "OK",
                                value = """
                                    {
                                        "article": {
                                            "slug": "283e60096c26aa3a39cf04712cdd1ff7",
                                            "title": "title",
                                            "description": "description",
                                            "body": "body"
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = [
                    Content(
                        schema = Schema(implementation = GenericErrorModel::class),
                        examples = [
                            ExampleObject(
                                name = "Not Found",
                                value = """
                                    {
                                        "errors": {
                                            "body": [
                                                "slug に該当する記事は見つかりませんでした"
                                            ]
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/api/articles/{slug}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getArticle(
        @Parameter(description = "記事の slug", required = true, schema = Schema(minLength = 32, maxLength = 32)) @Valid @PathVariable("slug") @Length(min = 32, max = 32) slug: String,
    ): ResponseEntity<SingleArticleResponse> {
        /**
         * 作成済記事の取得
         */
        val createdArticle = showArticleUseCase.execute(slug).getOrElse { throw ShowArticleUseCaseErrorException(it) }

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

    /**
     * 単一記事取得ユースケースがエラーを戻したときの Exception
     *
     * このクラスの例外が発生したときに、@ExceptionHandler で例外処理をおこなう
     *
     * @property error
     */
    data class ShowArticleUseCaseErrorException(val error: ShowArticleUseCase.Error) : Exception()

    /**
     * ShowArticleUseCaseErrorException をハンドリングする関数
     *
     * ShowArticleUseCase.Error の型に合わせてレスポンスを分岐させる
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = [ShowArticleUseCaseErrorException::class])
    fun onShowArticleUseCaseErrorException(e: ShowArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
        when (val error = e.error) {
            /**
             * 原因: slug に該当する記事が見つからなかった
             */
            is ShowArticleUseCase.Error.NotFoundArticleBySlug -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = listOf("${error.slug} に該当する記事は見つかりませんでした")
                    )
                ),
                HttpStatus.NOT_FOUND
            )

            /**
             * 原因: バリデーションエラー
             */
            is ShowArticleUseCase.Error.ValidationErrors -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = error.errors.map { it.message }
                    )
                ),
                HttpStatus.BAD_REQUEST
            )
        }
}
