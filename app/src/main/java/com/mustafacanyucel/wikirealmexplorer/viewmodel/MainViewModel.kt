package com.mustafacanyucel.wikirealmexplorer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafacanyucel.wikirealmexplorer.api.ApiResult
import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.repository.IWikiRepository
import com.mustafacanyucel.wikirealmexplorer.repository.implementation.MockWikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val wikiRepository: IWikiRepository): ViewModel() {

    private var _categoryList: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categoryList: StateFlow<List<Category>>
        get() = _categoryList.asStateFlow()

    private val _isSearching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean>
        get() = _isSearching.asStateFlow()

    fun fetchCategories(searchQuery: String? = null) {
        if (searchQuery.isNullOrEmpty()) {
            _categoryList.value = emptyList()
        } else {
            viewModelScope.launch {
                _isSearching.value = true
                wikiRepository.getCategories(searchQuery).collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            _categoryList.value = result.data
                        }
                        is ApiResult.Error -> {
                            Log.e("MainViewModel", "Error fetching categories: ${result.message}")
                            _categoryList.value = emptyList()
                            // TODO show error message to the user
                        }
                    }
                    _isSearching.value = false
                }
            }
        }
    }

    fun onSearch(searchQuery: String) {
        Log.d("MainViewModel", "onSearch: $searchQuery")
    }

}