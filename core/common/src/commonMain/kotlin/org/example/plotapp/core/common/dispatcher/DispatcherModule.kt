package org.example.plotapp.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule = module {
    // Dispatchers
    single<CoroutineDispatcher>(DispatcherIoScope) { Dispatchers.IO }
}

val DispatcherIoScope = named("IO")