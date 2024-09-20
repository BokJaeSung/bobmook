package com.example.emptywearapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.emptywearapp.R
import com.example.emptywearapp.presentation.theme.EmptywearAPpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Loading...")
        }

        // 네트워크 작업은 백그라운드에서 수행
        lifecycleScope.launch {
            val htmlData = fetchHtmlData("https://example.com")
            // HTML 데이터를 가져온 후 UI를 갱신
            setContent {
                WearApp(htmlData ?: "Failed to load")
            }
        }
    }

    // OkHttp로 HTML 데이터를 가져오는 함수
    private suspend fun fetchHtmlData(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                val html = response.body?.string()

                // Jsoup으로 HTML 데이터 파싱
                if (html != null) {
                    val document = Jsoup.parse(html)
                    val title = document.title()
                    Log.d("HTML Title", title)
                    return@withContext title // 예시로 제목을 가져옴
                }
                return@withContext null
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }
}

@Composable
fun WearApp(htmlContent: String) {
    EmptywearAPpTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = htmlContent
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("taewon")
}
