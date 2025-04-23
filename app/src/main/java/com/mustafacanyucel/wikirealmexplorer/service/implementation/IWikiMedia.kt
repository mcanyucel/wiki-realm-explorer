package com.mustafacanyucel.wikirealmexplorer.service.implementation

import com.mustafacanyucel.wikirealmexplorer.model.Category

interface IWikiMedia {
    suspend fun getCategories(query: String): List<Category>
}