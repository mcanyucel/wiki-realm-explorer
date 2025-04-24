package com.mustafacanyucel.wikirealmexplorer.repository.implementation

import android.util.Log
import com.mustafacanyucel.wikirealmexplorer.api.ApiHelper.Companion.getHttpErrorMessage
import com.mustafacanyucel.wikirealmexplorer.api.ApiResult
import com.mustafacanyucel.wikirealmexplorer.api.MediaWikiApi
import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class WikiRepository @Inject constructor(
    private val mediaWikiApi: MediaWikiApi
) : IWikiRepository {
    override suspend fun getCategories(query: String): Flow<ApiResult<List<Category>>> = flow {
        try {

            val categoryQueryResponse = mediaWikiApi.getCategories(srsearch = query)
            Log.d("WikiRepository", "Category query response: $categoryQueryResponse")

            val categories = categoryQueryResponse.wikiQuery.results.map { wikiCategory ->
                Category.fromWikiCategory(wikiCategory)
            }
            emit(ApiResult.Success(categories))
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val code = e.code()
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("AccountRepository", "HTTP Error $code: $errorBody", e)
                    "Server error (HTTP $code): ${getHttpErrorMessage(code, errorBody)}"
                }

                is UnknownHostException -> {
                    Log.e("AccountRepository", "Unknown host: ${e.message}", e)
                    "Cannot connect to server. Please check your internet connection and server URL."
                }

                is SocketTimeoutException -> {
                    Log.e("AccountRepository", "Connection timeout: ${e.message}", e)
                    "Connection timed out. The server took too long to respond."
                }

                is IOException -> {
                    Log.e("AccountRepository", "Network error: ${e.message}", e)
                    "Network error: ${e.message ?: "Unknown IO error"}"
                }

                else -> {
                    Log.e("AccountRepository", "Unexpected error: ${e.message}", e)
                    "Unexpected error: ${e.message ?: "Unknown error"}"
                }
            }

            val errorCode = if (e is HttpException) e.code() else null
            emit(ApiResult.Error(errorMessage, errorCode, e))

        }
    }
}