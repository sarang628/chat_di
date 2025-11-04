package com.sarang.torang.di.chat_di

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import com.sarang.torang.compose.chat.ChatScreen
import com.sarang.torang.compose.chat.LocalChatImageLoader
import com.sarang.torang.di.image.provideTorangAsyncImage
import com.sryang.torang.ui.TorangTheme
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
                CompositionLocalProvider(
                    LocalChatImageLoader provides CustomChatImageLoader
                ) {
                    ChatScreen(
                        onBack = { finish() },
                        roomId = roomId,
                    )
                }
            }
        }
    }

    companion object {
        fun go(context: Context, roomId: Int) {
            context.startActivity(Intent(context, ChatActivity::class.java).apply {
                putExtra("roomId", roomId)
            })
        }
    }
}


