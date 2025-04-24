package com.mustafacanyucel.wikirealmexplorer.repository.implementation

import com.mustafacanyucel.wikirealmexplorer.api.ApiResult
import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.random.Random


class MockWikiRepository @Inject constructor(): IWikiRepository {
    override suspend fun getCategories(query: String): Flow<ApiResult<List<Category>>> {
        // return a random number of strings starting with the given query
        delay(Random.nextLong(100, 700)) // Simulate HTTP API response time
        val itemCount = Random.nextInt(1, 10)

        val categories = (1..itemCount).map { index ->
            Category(title = "$query-$index",)
        }

        return flow {
            emit(ApiResult.Success(categories))
        }
    }
}