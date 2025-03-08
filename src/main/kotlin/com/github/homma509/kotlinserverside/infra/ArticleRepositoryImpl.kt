package com.github.homma509.kotlinserverside.infra

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.homma509.kotlinserverside.domain.ArticleRepository
import com.github.homma509.kotlinserverside.domain.ArticleRepository.FindBySlugError
import com.github.homma509.kotlinserverside.domain.Body
import com.github.homma509.kotlinserverside.domain.CreatedArticle
import com.github.homma509.kotlinserverside.domain.Description
import com.github.homma509.kotlinserverside.domain.Slug
import com.github.homma509.kotlinserverside.domain.Title
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * 作成済記事リポジトリの具象クラス
 *
 * @property namedParameterJdbcTemplate
 */
@Repository
class ArticleRepositoryImpl(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : ArticleRepository {
    override fun findBySlug(slug: Slug): Either<FindBySlugError, CreatedArticle> {
        val sql = """
            SELECT
                slug,
                title,
                body,
                description
            FROM
                articles
            WHERE
                slug = :slug
        """.trimIndent()
        val articleMapList = namedParameterJdbcTemplate.queryForList(sql, MapSqlParameterSource().addValue("slug", slug.value))

        /**
         * DB から作成済記事が見つからなかった場合、早期 return
         */
        if (articleMapList.isEmpty()) {
            return ArticleRepository.FindBySlugError.NotFound(slug = slug).left()
        }

        val articleMap = articleMapList.first()
        return CreatedArticle.newWithoutValidation(
            slug = Slug.newWithoutValidation(articleMap["slug"].toString()),
            title = Title.newWithoutValidation(articleMap["title"].toString()),
            body = Body.newWithoutValidation(articleMap["body"].toString()),
            description = Description.newWithoutValidation(articleMap["description"].toString())
        ).right()
    }
}
