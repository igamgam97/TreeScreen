package org.example.plotapp

import org.example.plotapp.core.data.di.dataModule
import org.example.plotapp.core.domain.di.domainModule
import org.example.plotapp.core.network.di.networkModule
import org.example.plotapp.feature.hierarchyeditor.di.hierarchyEditorModule
import org.koin.dsl.module

val appModule = module {
    includes(
        networkModule,
        dataModule,
        domainModule,
        hierarchyEditorModule,
    )
}