
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
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
            val htmlData = fetchMealMenu("https://dorm.knu.ac.kr/newlife/newlife_04.php?get_mode=2")
            // HTML 데이터를 가져온 후 UI를 갱신
            setContent {
                WearApp(htmlData ?: "Failed to load")
            }
        }
    }

    private suspend fun fetchMealMenu(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                val html = response.body?.string()

                if (html != null) {
                    val document = Jsoup.parse(html)
                    val menuTable = document.select(".today_menu").first() // first()를 먼저 호출

                    val mealMenu = StringBuilder()

                    // menuTable이 null이 아닐 때만 처리
                    menuTable?.let {
                        for (row in it.select("tr")) {
                            val mealType = row.select("td.txt_left").text() // 첫 번째 열 (아침, 점심, 저녁)
                            val mealDescription = row.select("td.txt_right").text() // 두 번째 열 (메뉴)

                            // 메뉴 정보 포맷팅
                            mealMenu.append("$mealType: $mealDescription\n")
                        }
                    } ?: run {
                        // 메뉴 테이블이 null일 경우 처리 (예: 에러 메시지)
                        return@withContext "메뉴 정보를 찾을 수 없습니다."
                    }

                    return@withContext mealMenu.toString()
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
