package com.mustafacanyucel.wikirealmexplorer.model

import com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category.WikiCategory

data class Category(val title: String) {
    companion object {
        fun fromWikiCategory(wikiCategory: WikiCategory): Category {
            return Category(title = wikiCategory.formattedTitle)
        }
    }
}
