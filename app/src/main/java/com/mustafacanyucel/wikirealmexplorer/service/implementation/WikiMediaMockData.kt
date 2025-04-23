package com.mustafacanyucel.wikirealmexplorer.service.implementation

import kotlinx.coroutines.delay
import kotlin.random.Random
import com.mustafacanyucel.wikirealmexplorer.model.Category

class WikiMediaMockData : IWikiMedia {
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