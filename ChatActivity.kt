package com.sarang.torang.di.chat_di

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.samples.apps.sunflower.ui.TorangTheme
import com.sarang.torang.compose.chat.ChatScreen
import com.sarang.torang.di.image.provideTorangAsyncImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getIntExtra("userId", -1)
        val roomId = intent.getIntExtra("roomId", -1)

        Log.d("__ChatActivity", "userId : $userId")
        Log.d("__ChatActivity", "roomId : $roomId")

        setContent {
            TorangTheme {
                ChatScreen(
                    onBack = {
                        finish()
                    }, image = provideTorangAsyncImage(),
                    userId = userId,
                    roomId = roomId
                )
            }
        }
    }
}


