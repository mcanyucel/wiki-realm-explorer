package com.mustafacanyucel.wikirealmexplorer.repository

import com.mustafacanyucel.wikirealmexplorer.model.Category

interface IWikiRepository {
    suspend fun getCategories(query: String): List<Category>
}