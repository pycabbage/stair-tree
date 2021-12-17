package com.example.stairtree.ui.map.detail

object MapDetailObject {
    val level1Message = listOf(
        MapDetailEntity(
            country = "アメリカ",
            title = "アメリカで発生した地球温暖化の事例",
            message = "地球温暖化の影響により、アメリカに上陸する台風の速度が低下し、被害が増加します。",
            countryJson = "usa",
            articleURL = "https://news.yahoo.co.jp/articles/11c88a61bd1befde464bcaedac41af8cbdce56f8",
            latitude = 37.0902,
            longitude = -100.7128,
            zoom = 3f,
        ),
        MapDetailEntity(
            country = "ツバル",
            title = "ツバル",
            message = "ツバルでは地球温暖化の影響により海面上昇が起こっています。",
            countryJson = "tuv",
            articleURL = "https://news.yahoo.co.jp/articles/d2855b6cc325c8cf03bca4ef0b308968e65a5b42",
            latitude = -8.516667,
            longitude = 179.216667,
            zoom = 8f,
        ),
        MapDetailEntity(
            country = "バングラデシュ",
            title = "バングラデシュ",
            message = "地球温暖化によるサイクロンにより、多くの死者が出ています。",
            countryJson = "bgd",
            articleURL = "https://news.yahoo.co.jp/byline/morisayaka/20210526-00239929",
            latitude = 22.7,
            longitude = 90.35,
            zoom = 5f,
        )
    )
    val level1size = level1Message.size
    val level2Message = listOf(
        MapDetailEntity(
            country = "グリーンランド",
            title = "グリーンランドについて",
            message = "グリーンランドは、2100年までに海面が60cm上昇するといわれています。",
            countryJson = "grl",
            articleURL = "https://news.yahoo.co.jp/byline/morisayaka/20190807-00136984",
            latitude = 71.7069,
            longitude = -42.6043,
            zoom = 3f,
        ),
        MapDetailEntity(
            country = "日本",
            title = "日本について",
            message = "100年後の日本では、風速が70[m/s]近くある「スーパー台風」の上陸数が現在の4倍に増えます。",
            countryJson = "jpn",
            articleURL = "https://news.yahoo.co.jp/articles/8b7a81cc957871b8045082d446e266e470d937a1",
            latitude = 33.0,
            longitude = 138.0,
            zoom = 4f,
        )
    )
    val level2size = level2Message.size
}