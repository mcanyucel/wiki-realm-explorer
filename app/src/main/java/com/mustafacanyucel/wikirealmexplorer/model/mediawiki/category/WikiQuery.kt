package com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category

import com.google.gson.annotations.SerializedName

data class WikiQuery(
    @SerializedName("searchinfo") val searchInfo: WikiSearchInfo,
    @SerializedName("search") val results: List<WikiCategory>
)
