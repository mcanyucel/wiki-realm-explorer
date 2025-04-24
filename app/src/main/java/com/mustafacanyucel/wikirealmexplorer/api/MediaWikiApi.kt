package com.mustafacanyucel.wikirealmexplorer.api

import com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category.CategoryQueryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaWikiApi {
    @GET ("/w/api.php")
    suspend fun getCategories(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") srsearch: String,
        @Query("format") format: String = "json",
        @Query("srlimit") srlimit: Int = 25,
        @Query("srnamespace") srnamespace: Int = 14,
    ): CategoryQueryResponse
}