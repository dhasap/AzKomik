package com.azkomik.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * Helper function to safely execute API calls
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Response body is null"))
            }
        } else {
            Result.failure(Exception("API call failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Extension function to check if network error is a timeout
 */
fun Exception.isTimeout(): Boolean {
    return message?.contains("timeout", ignoreCase = true) == true
}

/**
 * Extension function to check if network error is no connection
 */
fun Exception.isNoConnection(): Boolean {
    return message?.contains("unable to resolve host", ignoreCase = true) == true ||
            message?.contains("network", ignoreCase = true) == true
}
