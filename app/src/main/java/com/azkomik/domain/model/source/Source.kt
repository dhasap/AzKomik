package com.azkomik.domain.model.source

import androidx.compose.ui.graphics.Color

data class Source(
    val id: Long,
    val name: String,
    val lang: String,
    val iconUrl: String? = null,
    val isEnabled: Boolean = true,
    val isInstalled: Boolean = true,
    val isNsfw: Boolean = false,
    val versionId: Int = 1,
    val apiVersion: String = "1.0",
    val supportsLatest: Boolean = true,
    val supportsSearch: Boolean = true,
    val lastUsed: Long = System.currentTimeMillis(),
    val mangaCount: Int = 0
) {
    val langDisplay: String
        get() = when (lang) {
            "en" -> "English"
            "id" -> "Indonesia"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "zh" -> "Chinese"
            else -> lang.uppercase()
        }
    
    val flagEmoji: String
        get() = when (lang) {
            "en" -> "🇬🇧"
            "id" -> "🇮🇩"
            "ja" -> "🇯🇵"
            "ko" -> "🇰🇷"
            "zh" -> "🇨🇳"
            else -> "🌐"
        }
}

data class SourceFilter(
    val name: String,
    val state: FilterState = FilterState.Header("")
)

sealed class FilterState {
    data class Header(val name: String) : FilterState()
    data object Separator : FilterState()
    data class Text(val name: String, val value: String = "") : FilterState()
    data class Checkbox(val name: String, val value: Boolean = false) : FilterState()
    data class TriState(val name: String, val value: Int = STATE_IGNORE) : FilterState() {
        companion object {
            const val STATE_IGNORE = 0
            const val STATE_INCLUDE = 1
            const val STATE_EXCLUDE = 2
        }
    }
    data class Select(
        val name: String,
        val values: List<String>,
        val selectedIndex: Int = 0
    ) : FilterState()
    data class Group(
        val name: String,
        val state: List<FilterState>
    ) : FilterState()
    data class Sort(
        val name: String,
        val values: List<Pair<String, Boolean>>, // Pair of name to ascending
        val selectedIndex: Int = 0
    ) : FilterState()
}

data class CatalogSource(
    val source: Source,
    val mangaCount: Int = 0,
    val lastFetched: Long? = null
)

data class Extension(
    val name: String,
    val pkgName: String,
    val versionName: String,
    val versionCode: Int,
    val sources: List<Source>,
    val iconUrl: String? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false,
    val isObsolete: Boolean = false,
    val lang: String = "all"
)
