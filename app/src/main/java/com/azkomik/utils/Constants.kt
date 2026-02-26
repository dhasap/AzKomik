package com.azkomik.utils

object Constants {
    const val BASE_URL = "https://api.azkomik.com/v1/"
    
    // Database
    const val DATABASE_NAME = "azkomik_database"
    const val DATABASE_VERSION = 1
    
    // Preferences
    const val PREFS_NAME = "azkomik_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_READER_MODE = "reader_mode"
    
    // API Endpoints
    const val ENDPOINT_POPULAR = "manga/popular"
    const val ENDPOINT_SEARCH = "manga/search"
    const val ENDPOINT_LATEST = "manga/latest"
    
    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 1
}
