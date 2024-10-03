package com.sarang.torang.di.chat_di

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.google.samples.apps.sunflower.ui.TorangTheme
import com.sarang.instagralleryModule.GalleryNavHost
import com.sarang.torang.compose.bottomsheet.ImageSelectBottomSheetScaffold
import com.sarang.torang.compose.chat.ChatScreen
import com.sarang.torang.di.image.provideTorangAsyncImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val roomId = intent.getIntExtra("roomId", -1)
        Log.d("__ChatActivity", "roomId : $roomId")

        setContent {
            TorangTheme {
                ChatScreen(
                    onBack = { finish() },
                    image = provideTorangAsyncImage(),
                    roomId = roomId,
                    galleryCompose = {
                        GalleryNavHost(onNext = {}, onClose = { /*TODO*/ }, onBack = {})
                    },
                    galleryBottomSheetScaffoldCompose =
                    { show, onHidden, sheetContent, content ->
                        ImageSelectBottomSheetScaffold(
                            show = show,
                            onHidden = onHidden,
                            imageSelectCompose = sheetContent,
                            content = content
                        )
                    }
                )
            }
        }
    }
}


