package com.mustafacanyucel.wikirealmexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.ui.theme.WikiRealmExplorerTheme
import com.mustafacanyucel.wikirealmexplorer.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.fadeIn as fadeIn1

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WikiRealmExplorerTheme {
                MainScreen()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val categoryList by viewModel.categoryList.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    // Get window insets to handle the status bar properly
    val windowInsets = WindowInsets.systemBars
    val topPadding = with(LocalDensity.current) { windowInsets.getTop(this).toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        // The main scaffold without the searchbar in topBar
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // Empty TopAppBar to reserve space but not contain the search bar
                TopAppBar(
                    title = { },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    // Set window insets to avoid overlapping with system UI
                    windowInsets = WindowInsets.systemBars
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Text(text = "Main Screen")
                }
            }
        )

        // Overlay the search bar at the top, respecting system insets
        SearchBarWithAutocomplete(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPadding) // Add padding for status bar
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .zIndex(1f), // Ensure it's above other content
            searchResults = categoryList,
            isSearching = isSearching,
            fetchSearchResults = { query -> viewModel.fetchCategories(query) },
            onSearch = { query -> viewModel.onSearch(query) }
        )
    }
}

@Composable
fun SearchBarWithAutocomplete(
    modifier: Modifier = Modifier,
    searchResults: List<Category>,
    isSearching: Boolean,
    fetchSearchResults: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var isDropdownVisible by remember { mutableStateOf(false) }

    // Create a proper debounced search function
    val debouncedSearch = remember {
        var searchJob: Job? = null
        { text: String ->
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(300) // 300ms debounce delay
                fetchSearchResults(text)
            }
        }
    }

    // Control dropdown visibility based on focus, text, and results
    LaunchedEffect(isFocused, searchText, searchResults) {
        isDropdownVisible = isFocused && searchText.isNotBlank() && searchResults.isNotEmpty()
    }

    Box(modifier = modifier) {
        // The search text field
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                if (newText.isNotBlank()) {
                    debouncedSearch(newText)
                } else {
                    fetchSearchResults("")
                }
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (isSearching) {
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSearching) 1f else 0f,
                        animationSpec = tween(500),
                        label = ""
                    )
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(24.dp)
                            .alpha(animatedAlpha),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                        fetchSearchResults("")
                        focusRequester.requestFocus()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            placeholder = { Text("Search a Category") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(searchText)
                    focusManager.clearFocus()
                }
            ),
            interactionSource = interactionSource
        )

        // The dropdown surface positioned absolutely
        AnimatedVisibility(
            visible = isDropdownVisible,
            enter = fadeIn1() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp) // Position below the text field
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(searchResults) { result ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchText = result.title
                                    onSearch(result.title)
                                    focusManager.clearFocus()
                                },
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = result.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                        if (searchResults.indexOf(result) < searchResults.size - 1) {
                            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }

    // Request focus when the composable is first created
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun SearchBarWithAutocompletePreview() {
    SearchBarWithAutocomplete(
        searchResults = listOf(
            Category(
                "Category 1"
            ),
            Category(
                "Category 2"
            )
        ),
        isSearching = false,
        fetchSearchResults = {},
        onSearch = {}
    )
}


