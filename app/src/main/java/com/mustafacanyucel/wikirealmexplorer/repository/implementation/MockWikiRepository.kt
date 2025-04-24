package com.mustafacanyucel.wikirealmexplorer.repository.implementation

import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.random.Random


class MockWikiRepository @Inject constructor(): IWikiRepository {
    override suspend fun getCategories(query: String): List<Category> {
        // return a random number of strings starting with the given query
        delay(Random.nextLong(100, 700)) // Simulate HTTP API response time
        val itemCount = Random.nextInt(1, 10) // Generate a random number of items
        return (1..itemCount).map {
            val randomString = (1..5).map { ('a' + Random.nextInt(0, 26)) }.joinToString("")
            Category(
                title = "$query$randomString",
            )
        }
    }
}