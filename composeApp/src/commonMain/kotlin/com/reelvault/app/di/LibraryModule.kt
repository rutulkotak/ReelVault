package com.reelvault.app.di

import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.domain.usecase.SaveReelFromUrlUseCase
import com.reelvault.app.domain.usecase.SaveReelUseCase
import com.reelvault.app.presentation.library.LibraryViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for the Library feature.
 * Provides UseCases and ViewModel dependencies.
 */
val libraryModule = module {
    // UseCases
    factoryOf(::GetSavedReelsUseCase)
    factoryOf(::SaveReelUseCase)
    factoryOf(::SaveReelFromUrlUseCase)

    // ViewModel
    viewModelOf(::LibraryViewModel)
}
