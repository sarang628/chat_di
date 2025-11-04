package com.sarang.torang.di.chat_di

import com.sarang.torang.compose.chat.ChatImageLoaderType
import com.sarang.torang.di.image.provideTorangAsyncImage

val CustomChatImageLoader : ChatImageLoaderType = {
    provideTorangAsyncImage().invoke(
        it.modifier,
        it.url,
        it.iconSize,
        it.progressSize,
        it.contentScale
    )
}