package com.mustafacanyucel.wikirealmexplorer.model.mediawiki.category

import com.google.gson.annotations.SerializedName

class WikiCategory(
    val ns: Int,
    private val title: String,
    @SerializedName("pageid") val pageId: Int,
    val size: Int,
    @SerializedName("wordcount") val wordCount: Int,
    val snippet: String,
    val timestamp: String
) {
    /**
     * All titles returned by the MediaWiki API are formatted as "Category:Title"; this
     * property removes the "Category:" prefix
     */
    val formattedTitle: String
        get() = title.substring(9)
}