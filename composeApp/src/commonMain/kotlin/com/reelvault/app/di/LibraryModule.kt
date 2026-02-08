package com.reelvault.app.di

import com.reelvault.app.data.featuregate.FeatureGateImpl
import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.usecase.CreateCollectionUseCase
import com.reelvault.app.domain.usecase.DeleteCollectionUseCase
import com.reelvault.app.domain.usecase.DeleteReelsUseCase
import com.reelvault.app.domain.usecase.GetCollectionsUseCase
import com.reelvault.app.domain.usecase.GetReelsByCollectionUseCase
import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.domain.usecase.MoveReelsToCollectionUseCase
import com.reelvault.app.domain.usecase.SaveReelFromUrlUseCase
import com.reelvault.app.domain.usecase.SaveReelUseCase
import com.reelvault.app.domain.usecase.UpdateReelDetailsUseCase
import com.reelvault.app.presentation.collections.CollectionsViewModel
import com.reelvault.app.presentation.library.LibraryViewModel
import com.reelvault.app.presentation.tiers.TierSelectionViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the Library feature.
 * Provides UseCases and ViewModel dependencies.
 */
val libraryModule = module {
    // Feature Gate
    single { FeatureGateImpl() } bind FeatureGate::class

    // Reel UseCases
    factoryOf(::GetSavedReelsUseCase)
    factoryOf(::SaveReelUseCase)
    factoryOf(::SaveReelFromUrlUseCase)
    factoryOf(::DeleteReelsUseCase)
    factoryOf(::UpdateReelDetailsUseCase)
    factoryOf(::MoveReelsToCollectionUseCase)
    factoryOf(::GetReelsByCollectionUseCase)

    // Collection UseCases
    factoryOf(::GetCollectionsUseCase)
    factoryOf(::CreateCollectionUseCase)
    factoryOf(::DeleteCollectionUseCase)

    // ViewModels
    viewModelOf(::LibraryViewModel)
    viewModelOf(::CollectionsViewModel)
    viewModelOf(::TierSelectionViewModel)
}
