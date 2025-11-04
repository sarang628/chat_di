package com.sarang.torang.di.chat_di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.sarang.instagralleryModule.compose.GalleryBottomSheet
import com.sarang.torang.compose.chat.GalleryBottomSheetScaffoldType

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val CustomGalleryBottomSheet : GalleryBottomSheetScaffoldType = @Composable {
    GalleryBottomSheet(
            imageSelectBottomSheetScaffold = { show, onHidden, sheetContent, content ->
                /*ImageSelectBottomSheetScaffold(
                    show = show,
                    onHidden = onHidden,
                    imageSelectCompose = sheetContent,
                    content = content
                )*/
            },
            onSend = it.onSend,
            show = it.show,
            onHidden = it.onHidden,
            onBack = {},
            content = it.content
        )
}