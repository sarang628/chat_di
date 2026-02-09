package com.sarang.torang.di.chat_di

import androidx.compose.ui.unit.dp
import com.sarang.torang.compose.chat.ChatImageLoaderType
import com.sarang.torang.di.image.TorangAsyncImageData
import com.sarang.torang.di.image.provideTorangAsyncImage

val CustomChatImageLoader : ChatImageLoaderType = {
    provideTorangAsyncImage().invoke(
        TorangAsyncImageData(
            it.modifier,
            it.url,
            it.progressSize ?: 30.dp,
            it.iconSize ?: 30.dp,
            it.contentScale
        )
    )
}