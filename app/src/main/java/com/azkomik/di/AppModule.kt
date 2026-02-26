package com.azkomik.di

import android.content.Context
import androidx.room.Room
import com.azkomik.data.local.database.AppDatabase
import com.azkomik.data.remote.api.MangaApiService
import com.azkomik.data.repository.MangaRepositoryImpl
import com.azkomik.domain.repository.MangaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://api.azkomik.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideMangaApiService(retrofit: Retrofit): MangaApiService {
        return retrofit.create(MangaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "azkomik_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMangaDao(database: AppDatabase) = database.mangaDao()

    @Provides
    @Singleton
    fun provideChapterDao(database: AppDatabase) = database.chapterDao()

    @Provides
    @Singleton
    fun provideMangaRepository(
        mangaDao: com.azkomik.data.local.dao.MangaDao,
        chapterDao: com.azkomik.data.local.dao.ChapterDao,
        apiService: MangaApiService
    ): MangaRepository {
        return MangaRepositoryImpl(mangaDao, chapterDao, apiService)
    }
}
