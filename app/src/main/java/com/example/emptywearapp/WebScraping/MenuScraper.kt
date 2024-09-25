package com.example.emptywearapp.WebScraping

import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class  Menu(
    val breakfast: String,
    val lunch: String,
    val dinner: String
)

class MenuScraper {

    // 현재 날짜를 받아오는 함수
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun scrapeMenus(url: String): Menu? {
        return try {
            val currentDate = getCurrentDate();
            val document = Jsoup.connect(url).get()

            // 특정 날짜에 해당하는 div 선택
            val menuBox = document.selectFirst("div#menu_$currentDate")
                ?: throw IllegalArgumentException("메뉴 박스를 찾을 수 없습니다.")

            // 테이블 선택
            val table: Elements = menuBox.select("table.courTable.menu_boxa") // 해당 테이블의 클래스는 courTable menu_boxa임..
                ?: throw IllegalArgumentException("메뉴 테이블을 찾을 수 없습니다.")

            // 메뉴가 포함된 두 번째 tr 선택 (첫 번째 tr은 헤더)
            val menuRow = table.select("tbody > tr").getOrNull(1)
                ?: throw IllegalArgumentException("메뉴 행을 찾을 수 없습니다.")

            //각 td 요소 선택 (아침, 점심, 저녁)
            val tds: Elements = menuRow.select("td")
            if (tds.size < 3) {
                throw IllegalArgumentException("메뉴 항목이 부족합니다.")
            }

            // 각 메뉴 텍스트 추출
            val breakfast = tds[0].text().trim()
            val launch = tds[1].text().trim()
            val dinner = tds[2].text().trim()

            // Menu 객체 생성하여 반환
            Menu(breakfast, launch, dinner)
        } catch (e: IOException) {
            e.printStackTrace()
            null // 스크래핑 실패시 null 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null // 기타 예외 처리
        }
    }
}