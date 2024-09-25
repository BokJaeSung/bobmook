/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.emptywearapp.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.emptywearapp.R
import com.example.emptywearapp.WebScraping.MenuScraper
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    private lateinit var scraper: MenuScraper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scraper = MenuScraper()

        val scrapeButton: Button = findViewById(R.id.scrape_button)
        scrapeButton.setOnClickListener {
            scrapeMenus()
        }
    }

    private fun scrapeMenus() {
        val url = "https://dorm.knu.ac.kr/newlife/newlife_04.php?get_mode=2"

        Thread {
            val menu = scraper.scrapeMenus(url);
            runOnUiThread {
                if (menu != null) {
                    // UI 업데이트 (TextView에 메뉴 표시)
                    findViewById<TextView>(R.id.breakfast_text).text = menu.breakfast
                    findViewById<TextView>(R.id.lunch_text).text = menu.lunch
                    findViewById<TextView>(R.id.dinner_text).text = menu.dinner
                } else {
                    Toast.makeText(this, "메뉴를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}

