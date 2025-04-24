package com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category

import com.google.gson.annotations.SerializedName

data class CategoryQueryResponse(
    @SerializedName("batchcomplete") val batchComplete: String,
    @SerializedName("continue") val wikiContinue: WikiContinue,
    @SerializedName("query") val wikiQuery: WikiQuery,
)
