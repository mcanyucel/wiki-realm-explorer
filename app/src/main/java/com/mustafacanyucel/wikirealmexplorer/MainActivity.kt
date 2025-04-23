package com.mustafacanyucel.wikirealmexplorer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafacanyucel.wikirealmexplorer.model.Category
import com.mustafacanyucel.wikirealmexplorer.ui.theme.WikiRealmExplorerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WikiRealmExplorerTheme {
                WikiRealmExplorerApp()
            }
        }
    }
}

@Composable
fun WikiRealmExplorerApp() {

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SearchBarWithAutocomplete(

            ) { }
        }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WikiRealmExplorerTheme {
        Greeting("Android")
    }
}

@Composable
fun SearchBarWithAutocomplete(
    modifier: Modifier = Modifier,
    fetchSearchResults: suspend (String) -> List<Category>,
    onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Category>>(emptyList()) }
    val focusManager = LocalFocusManager.current
    var debounceJob: Job? by remember { mutableStateOf(null) }
    var coroutineScope = remember { CoroutineScope(Dispatchers.Main) }

    Column(modifier = modifier) {
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                debounceJob?.cancel()
                if(newText.isNotBlank()) {
                    debounceJob = coroutineScope.launch {
                        isSearching = true
                        delay(300)
                        searchResults = fetchSearchResults(newText)
                        isSearching = false
                    }
                }
                else {
                    searchResults = emptyList()
                }
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (isSearching) {
                    val animatedAlpha by animateFloatAsState(
                        targetValue = if (isSearching) 1f else 0f,
                        animationSpec = tween(500), label = ""
                    )
                    CircularProgressIndicator(
                        modifier = modifier
                            .width(24.dp)
                            .alpha(animatedAlpha),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp

                    )
                }
                else if (searchText.isNotEmpty()){
                    IconButton(onClick = {
                        searchText = ""
                        searchResults = emptyList()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Clear")
                    }
                }
            },
            placeholder = { Text("Search a Category") },
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType =  KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(searchText)
                    focusManager.clearFocus()
                }
            )
        )

        AnimatedVisibility(visible = searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                items(searchResults) { result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = result.title)
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun SearchBarWithAutocompletePreview() {
    SearchBarWithAutocomplete(
        onSearch = { query ->
            Log.d("SearchBar", "Searching for: $query")
        },
        fetchSearchResults = { query ->
            // Simulate an API call delay
            delay(500)
            // Simulate API response
            if(query == "a")
                listOf(
                    Category("Apple"),
                    Category("Apricot"),
                    Category("Avocado")
                )
            else if(query == "b")
                listOf(
                    Category("Banana"),
                    Category("Blackberry"),
                    Category("Blueberry")
                )
            else emptyList()
        }
    )
}