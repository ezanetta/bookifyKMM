package com.ezanetta.bookifykmm.di

import com.ezanetta.bookifykmm.data.network.BookApiService
import com.ezanetta.bookifykmm.data.network.OpenLibraryApiService
import com.ezanetta.bookifykmm.data.repository.BookRepositoryImpl
import com.ezanetta.bookifykmm.data.repository.WishlistRepositoryImpl
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.DetailViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
    singleOf(::OpenLibraryApiService) bind BookApiService::class
}

val repositoryModule = module {
    singleOf(::BookRepositoryImpl) bind BookRepository::class
    singleOf(::WishlistRepositoryImpl) bind WishlistRepository::class
}

val viewModelModule = module {
    factory { BookifyViewModel(get(), get()) }
    factory { DetailViewModel(get()) }
    factory { SettingsViewModel() }
}

val appModules = listOf(networkModule, repositoryModule, viewModelModule)
