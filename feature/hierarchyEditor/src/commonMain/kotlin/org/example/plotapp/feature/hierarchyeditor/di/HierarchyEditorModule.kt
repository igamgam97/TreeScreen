package org.example.plotapp.feature.hierarchyeditor.di

import org.example.plotapp.core.common.dispatcher.DispatcherIoScope
import org.example.plotapp.feature.hierarchyeditor.data.source.cache.TreeStateCache
import org.example.plotapp.feature.hierarchyeditor.data.source.cache.TreeStateCacheImpl
import org.example.plotapp.feature.hierarchyeditor.data.source.database.NodeDbSource
import org.example.plotapp.feature.hierarchyeditor.data.source.database.NodeDbSourceImpl
import org.example.plotapp.feature.hierarchyeditor.data.source.operation.PendingOperationsQueue
import org.example.plotapp.feature.hierarchyeditor.data.source.operation.PendingOperationsQueueImpl
import org.example.plotapp.feature.hierarchyeditor.domain.HierarchyCacheCoordinator
import org.example.plotapp.feature.hierarchyeditor.presentation.HierarchyViewModel
import org.example.plotapp.feature.hierarchyeditor.presentation.HierchyEntityMapper
import org.example.plotapp.feature.hierarchyeditor.presentation.TreeCacheMapper
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Dependency injection module for the hierarchy editor feature.
 */
val hierarchyEditorModule = module {
    singleOf(::HierarchyCacheCoordinator) {
        DispatcherIoScope
    }
    singleOf(::NodeDbSourceImpl) bind NodeDbSource::class
    singleOf(::HierchyEntityMapper)
    singleOf(::TreeCacheMapper)
    singleOf(::TreeStateCacheImpl) bind TreeStateCache::class
    singleOf(::PendingOperationsQueueImpl) bind PendingOperationsQueue::class

    // ViewModel
    viewModelOf(::HierarchyViewModel)
}