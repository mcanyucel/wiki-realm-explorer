package com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category

import com.google.gson.annotations.SerializedName

data class WikiSearchInfo(
    @SerializedName("totalhits") val totalHits: Int,
)
