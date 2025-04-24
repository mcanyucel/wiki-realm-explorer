package com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category

import com.google.gson.annotations.SerializedName

data class WikiContinue(
    @SerializedName("sroffset") val offsetCount: Int,
    @SerializedName("continue") val continueString: String
)
