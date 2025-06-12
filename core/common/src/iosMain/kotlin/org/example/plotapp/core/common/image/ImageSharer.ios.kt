package org.example.plotapp.core.common.image

import org.koin.dsl.module

actual val imageSharerModule = module {
    single<ImageSharer> { ImageSharerImpl() }
}