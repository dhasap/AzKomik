package com.azkomik.domain.model.reader

enum class ReadingMode {
    PAGED_LEFT_TO_RIGHT,
    PAGED_RIGHT_TO_LEFT,
    VERTICAL_SCROLL,
    WEBTOON,
    CONTINUOUS_VERTICAL
}

enum class ScaleType {
    FIT_SCREEN,
    STRETCH,
    FIT_WIDTH,
    FIT_HEIGHT,
    SMART_FIT
}

enum class NavigationMode {
    L_SHAPE,           // Default: bottom-right corner
    EDGE,              // Tap edges to navigate
    KINDLE,            // Large left/right areas
    DISABLED           // No tap navigation
}

data class ReaderSettings(
    val readingMode: ReadingMode = ReadingMode.PAGED_LEFT_TO_RIGHT,
    val scaleType: ScaleType = ScaleType.FIT_SCREEN,
    val navigationMode: NavigationMode = NavigationMode.L_SHAPE,
    val invertHorizontal: Boolean = false,
    val invertVertical: Boolean = false,
    val zoomStart: ZoomStart = ZoomStart(),
    val cropBorders: Boolean = false,
    val webtoonCropBorders: Boolean = false,
    val pageTransitions: Boolean = true,
    val hidePageNumbers: Boolean = false,
    val hideStatusBar: Boolean = true,
    val keepScreenOn: Boolean = true,
    val showReadingModeBanner: Boolean = true,
    val automaticBrightness: Boolean = true,
    val customBrightness: Float = 0.5f,
    val colorFilter: ColorFilter = ColorFilter(),
    val grayscale: Boolean = false,
    val invertedColors: Boolean = false,
    val dualPageSplit: Boolean = false,
    val dualPageInvert: Boolean = false
)

data class ZoomStart(
    val automatic: Boolean = true,
    val left: Boolean = false,
    val right: Boolean = false,
    val center: Boolean = false
)

data class ColorFilter(
    val enabled: Boolean = false,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val warmth: Float = 0f
)

enum class PageFilterType {
    ALL,
    DOWNLOADED,
    UNREAD,
    BOOKMARKED,
    HAS_COMMENTS
}

enum class PageSortType {
    SOURCE_ORDER,
    NEWEST_FIRST,
    OLDEST_FIRST
}

enum class ViewerNavigation {
    PREV,
    NEXT,
    MENU,
    NONE
}

enum class TappingInvertMode {
    NONE,
    HORIZONTAL,
    VERTICAL,
    BOTH
}
