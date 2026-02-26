package com.azkomik.presentation.screens.reader

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.azkomik.domain.model.Page
import com.azkomik.domain.model.reader.*
import com.azkomik.presentation.screens.reader.components.ChapterListSheet
import com.azkomik.presentation.screens.reader.components.ReaderSettingsSheet
import com.azkomik.presentation.theme.AppColors
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.ExperimentalFoundationPagerApi
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReaderScreen(
    chapterId: String,
    navController: NavController,
    viewModel: ReaderViewModel = hiltViewModel(),
    settingsViewModel: ReaderSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    var showControls by remember { mutableStateOf(true) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showChapterList by remember { mutableStateOf(false) }
    
    val settings = settingsState.settings
    val scope = rememberCoroutineScope()

    LaunchedEffect(chapterId) {
        viewModel.loadChapter(chapterId)
    }

    // Filter and sort pages
    val filteredPages = remember(uiState.pages, settingsState.currentFilter) {
        when (settingsState.currentFilter) {
            PageFilterType.ALL -> uiState.pages
            PageFilterType.DOWNLOADED -> uiState.pages // Would filter by download status
            PageFilterType.UNREAD -> uiState.pages // Would filter by read status
            PageFilterType.BOOKMARKED -> uiState.pages // Would filter by bookmark
            PageFilterType.HAS_COMMENTS -> uiState.pages // Would filter by comments
        }
    }

    val sortedPages = remember(filteredPages, settingsState.currentSort) {
        when (settingsState.currentSort) {
            PageSortType.SOURCE_ORDER -> filteredPages
            PageSortType.NEWEST_FIRST -> filteredPages.reversed()
            PageSortType.OLDEST_FIRST -> filteredPages
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AppColors.Primary
            )
        } else if (uiState.error != null) {
            ErrorView(
                message = uiState.error ?: "Error loading chapter",
                onRetry = { viewModel.loadChapter(chapterId) }
            )
        } else if (sortedPages.isNotEmpty()) {
            // Reader content based on mode
            when (settings.readingMode) {
                ReadingMode.PAGED_LEFT_TO_RIGHT -> {
                    PagedReader(
                        pages = sortedPages,
                        currentPage = uiState.currentPage,
                        scaleType = settings.scaleType,
                        navigationMode = settings.navigationMode,
                        onPageChange = { viewModel.setCurrentPage(it) },
                        onMenuToggle = { showControls = !showControls },
                        reverseLayout = false
                    )
                }
                ReadingMode.PAGED_RIGHT_TO_LEFT -> {
                    PagedReader(
                        pages = sortedPages,
                        currentPage = uiState.currentPage,
                        scaleType = settings.scaleType,
                        navigationMode = settings.navigationMode,
                        onPageChange = { viewModel.setCurrentPage(it) },
                        onMenuToggle = { showControls = !showControls },
                        reverseLayout = true
                    )
                }
                ReadingMode.VERTICAL_SCROLL -> {
                    VerticalScrollReader(
                        pages = sortedPages,
                        currentPage = uiState.currentPage,
                        scaleType = settings.scaleType,
                        onPageChange = { viewModel.setCurrentPage(it) },
                        onMenuToggle = { showControls = !showControls }
                    )
                }
                ReadingMode.WEBTOON -> {
                    WebtoonReader(
                        pages = sortedPages,
                        currentPage = uiState.currentPage,
                        cropBorders = settings.webtoonCropBorders,
                        onPageChange = { viewModel.setCurrentPage(it) },
                        onMenuToggle = { showControls = !showControls }
                    )
                }
                ReadingMode.CONTINUOUS_VERTICAL -> {
                    ContinuousVerticalReader(
                        pages = sortedPages,
                        currentPage = uiState.currentPage,
                        onPageChange = { viewModel.setCurrentPage(it) },
                        onMenuToggle = { showControls = !showControls }
                    )
                }
            }
            
            // Page indicator
            if (showControls) {
                PageIndicator(
                    currentPage = uiState.currentPage + 1,
                    totalPages = sortedPages.size,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 80.dp, end = 16.dp)
                )
            }
        }

        // Top Bar
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.mangaTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = "Chapter ${uiState.chapterNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showChapterList = true }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Chapters",
                            tint = AppColors.TextPrimary
                        )
                    }
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface.copy(alpha = 0.95f)
                )
            )
        }

        // Bottom Controls
        AnimatedVisibility(
            visible = showControls && sortedPages.isNotEmpty(),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReaderBottomControls(
                currentPage = uiState.currentPage,
                totalPages = sortedPages.size,
                readingMode = settings.readingMode,
                onPrevious = { viewModel.goToPreviousPage() },
                onNext = { viewModel.goToNextPage() },
                onPageSelected = { viewModel.setCurrentPage(it) },
                onReadingModeClick = { showSettingsSheet = true }
            )
        }
        
        // Reading mode banner
        AnimatedVisibility(
            visible = settings.showReadingModeBanner && showControls,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReadingModeBanner(mode = settings.readingMode)
        }
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        ReaderSettingsSheet(
            settings = settings,
            currentFilter = settingsState.currentFilter,
            currentSort = settingsState.currentSort,
            onDismiss = { showSettingsSheet = false },
            onReadingModeChange = settingsViewModel::setReadingMode,
            onScaleTypeChange = settingsViewModel::setScaleType,
            onNavigationModeChange = settingsViewModel::setNavigationMode,
            onToggleCropBorders = settingsViewModel::toggleCropBorders,
            onToggleWebtoonCrop = settingsViewModel::toggleWebtoonCropBorders,
            onTogglePageTransitions = settingsViewModel::togglePageTransitions,
            onToggleDualPage = settingsViewModel::toggleDualPageSplit,
            onFilterChange = settingsViewModel::setPageFilter,
            onSortChange = settingsViewModel::setPageSort
        )
    }
    
    // Chapter List Sheet
    if (showChapterList) {
        ChapterListSheet(
            chapters = emptyList(), // Would pass actual chapters
            currentChapterId = chapterId,
            onDismiss = { showChapterList = false },
            onChapterClick = { 
                showChapterList = false
                navController.navigate("reader/$it")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagedReader(
    pages: List<Page>,
    currentPage: Int,
    scaleType: ScaleType,
    navigationMode: NavigationMode,
    onPageChange: (Int) -> Unit,
    onMenuToggle: () -> Unit,
    reverseLayout: Boolean
) {
    val pagerState = rememberPagerState(initialPage = currentPage) { pages.size }
    
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.animateScrollToPage(currentPage)
        }
    }
    
    LaunchedEffect(pagerState.currentPage) {
        onPageChange(pagerState.currentPage)
    }

    HorizontalPager(
        state = pagerState,
        reverseLayout = reverseLayout,
        pageSize = PageSize.Fill,
        beyondBoundsPageCount = 1
    ) { pageIndex ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ReaderPage(
                imageUrl = pages[pageIndex].imageUrl,
                scaleType = scaleType,
                onTap = onMenuToggle
            )
        }
    }
}

@Composable
fun VerticalScrollReader(
    pages: List<Page>,
    currentPage: Int,
    scaleType: ScaleType,
    onPageChange: (Int) -> Unit,
    onMenuToggle: () -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(pages, key = { it.index }) { page ->
            ReaderPage(
                imageUrl = page.imageUrl,
                scaleType = scaleType,
                onTap = onMenuToggle
            )
        }
    }
}

@Composable
fun WebtoonReader(
    pages: List<Page>,
    currentPage: Int,
    cropBorders: Boolean,
    onPageChange: (Int) -> Unit,
    onMenuToggle: () -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(if (cropBorders) 0.dp else 2.dp)
    ) {
        items(pages, key = { it.index }) { page ->
            ReaderPage(
                imageUrl = page.imageUrl,
                scaleType = ScaleType.FIT_WIDTH,
                onTap = onMenuToggle
            )
        }
    }
}

@Composable
fun ContinuousVerticalReader(
    pages: List<Page>,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onMenuToggle: () -> Unit
) {
    // Similar to webtoon but with different scroll behavior
    WebtoonReader(
        pages = pages,
        currentPage = currentPage,
        cropBorders = false,
        onPageChange = onPageChange,
        onMenuToggle = onMenuToggle
    )
}

@Composable
fun ReaderPage(
    imageUrl: String,
    scaleType: ScaleType,
    onTap: () -> Unit
) {
    val contentScale = when (scaleType) {
        ScaleType.FIT_SCREEN -> ContentScale.Fit
        ScaleType.STRETCH -> ContentScale.FillBounds
        ScaleType.FIT_WIDTH -> ContentScale.FillWidth
        ScaleType.FIT_HEIGHT -> ContentScale.FillHeight
        ScaleType.SMART_FIT -> ContentScale.Fit
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Page",
            modifier = Modifier.fillMaxWidth(),
            contentScale = contentScale
        )
    }
}

@Composable
fun ReaderBottomControls(
    currentPage: Int,
    totalPages: Int,
    readingMode: ReadingMode,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPageSelected: (Int) -> Unit,
    onReadingModeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.Surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Slider
            Slider(
                value = currentPage.toFloat(),
                onValueChange = { onPageSelected(it.toInt()) },
                valueRange = 0f..(totalPages - 1).toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = AppColors.Primary,
                    activeTrackColor = AppColors.Primary,
                    inactiveTrackColor = AppColors.SurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Page numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${currentPage + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
                Text(
                    text = "$totalPages",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = currentPage > 0,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.TextPrimary,
                        disabledContentColor = AppColors.TextMuted
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev")
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Reading Mode button
                IconButton(
                    onClick = onReadingModeClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(AppColors.SurfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = when (readingMode) {
                            ReadingMode.WEBTOON -> Icons.Default.ViewAgenda
                            ReadingMode.VERTICAL_SCROLL -> Icons.Default.SwapVert
                            else -> Icons.Default.ViewCarousel
                        },
                        contentDescription = "Reading Mode",
                        tint = AppColors.TextPrimary
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Next button
                Button(
                    onClick = onNext,
                    enabled = currentPage < totalPages - 1,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        disabledContainerColor = AppColors.SurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AppColors.Surface.copy(alpha = 0.8f)
    ) {
        Text(
            text = "$currentPage / $totalPages",
            style = MaterialTheme.typography.labelMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ReadingModeBanner(mode: ReadingMode) {
    val text = when (mode) {
        ReadingMode.PAGED_LEFT_TO_RIGHT -> "Left to Right"
        ReadingMode.PAGED_RIGHT_TO_LEFT -> "Right to Left"
        ReadingMode.VERTICAL_SCROLL -> "Vertical"
        ReadingMode.WEBTOON -> "Webtoon"
        ReadingMode.CONTINUOUS_VERTICAL -> "Continuous"
    }
    
    Surface(
        modifier = Modifier.padding(top = 80.dp),
        shape = RoundedCornerShape(8.dp),
        color = AppColors.Surface.copy(alpha = 0.8f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = AppColors.Accent,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
