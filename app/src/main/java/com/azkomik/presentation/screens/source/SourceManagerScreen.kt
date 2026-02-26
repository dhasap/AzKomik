package com.azkomik.presentation.screens.source

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.azkomik.domain.model.source.Extension
import com.azkomik.domain.model.source.Source
import com.azkomik.presentation.theme.AppColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SourceManagerUiState(
    val installedSources: List<Source> = emptyList(),
    val availableExtensions: List<Extension> = emptyList(),
    val searchQuery: String = "",
    val selectedLanguage: String = "all",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SourceManagerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SourceManagerUiState())
    val uiState: StateFlow<SourceManagerUiState> = _uiState.asStateFlow()

    init {
        loadSources()
    }

    private fun loadSources() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock data
            val sources = listOf(
                Source(
                    id = 1,
                    name = "Shinigami",
                    lang = "id",
                    iconUrl = "https://picsum.photos/seed/shinigami/100/100",
                    isEnabled = true
                ),
                Source(
                    id = 2,
                    name = "Komiku",
                    lang = "id",
                    iconUrl = "https://picsum.photos/seed/komiku/100/100",
                    isEnabled = true
                ),
                Source(
                    id = 3,
                    name = "MangaDex",
                    lang = "en",
                    iconUrl = "https://picsum.photos/seed/mangadex/100/100",
                    isEnabled = true
                ),
                Source(
                    id = 4,
                    name = "MangaKakalot",
                    lang = "en",
                    iconUrl = "https://picsum.photos/seed/mangakakalot/100/100",
                    isEnabled = false
                )
            )
            
            _uiState.update {
                it.copy(
                    installedSources = sources,
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectLanguage(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun toggleSource(sourceId: Long) {
        _uiState.update { state ->
            state.copy(
                installedSources = state.installedSources.map { source ->
                    if (source.id == sourceId) {
                        source.copy(isEnabled = !source.isEnabled)
                    } else source
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceManagerScreen(
    viewModel: SourceManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sources", color = AppColors.TextPrimary) },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppColors.Background,
                contentColor = AppColors.Primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Installed") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Extensions") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Languages") }
                )
            }

            when (selectedTab) {
                0 -> InstalledSourcesTab(
                    sources = uiState.installedSources,
                    onToggleSource = viewModel::toggleSource
                )
                1 -> ExtensionsTab(extensions = uiState.availableExtensions)
                2 -> LanguagesTab()
            }
        }
    }
}

@Composable
fun InstalledSourcesTab(
    sources: List<Source>,
    onToggleSource: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sources) { source ->
            SourceItem(
                source = source,
                onToggle = { onToggleSource(source.id) }
            )
        }
    }
}

@Composable
fun SourceItem(
    source: Source,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source icon
            Surface(
                shape = CircleShape,
                color = AppColors.SurfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                AsyncImage(
                    model = source.iconUrl,
                    contentDescription = source.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = source.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "${source.flagEmoji} ${source.langDisplay}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
            
            Switch(
                checked = source.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppColors.Primary
                )
            )
        }
    }
}

@Composable
fun ExtensionsTab(extensions: List<Extension>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (extensions.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No extensions available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Check for updates */ }) {
                    Text("Check for Updates")
                }
            }
        } else {
            LazyColumn {
                items(extensions) { extension ->
                    // Extension item
                }
            }
        }
    }
}

@Composable
fun LanguagesTab() {
    val languages = listOf(
        "all" to "All Languages",
        "en" to "English",
        "id" to "Indonesian",
        "ja" to "Japanese",
        "ko" to "Korean",
        "zh" to "Chinese"
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(languages) { (code, name) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (code == "all") {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = AppColors.Primary
                    )
                }
            }
        }
    }
}

// Extension of items function for LazyListScope
private fun <T> LazyListScope.items(items: List<T>, itemContent: @Composable (T) -> Unit) {
    items(count = items.size) { index ->
        itemContent(items[index])
    }
}
