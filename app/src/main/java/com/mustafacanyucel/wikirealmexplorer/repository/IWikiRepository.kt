package com.mustafacanyucel.wikirealmexplorer.repository

import com.mustafacanyucel.wikirealmexplorer.api.ApiResult
import com.mustafacanyucel.wikirealmexplorer.model.Category
import kotlinx.coroutines.flow.Flow

interface IWikiRepository {
    suspend fun getCategories(query: String): Flow<ApiResult<List<Category>>>
}