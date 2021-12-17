package com.example.stairtree.ui.map.detail

object MapDetailObject {
    val level1Message = listOf(
        MapDetailEntity(
            country = "アメリカ",
            title = "アメリカで発生した地球温暖化の事例",
            message = "アメリカでは死者３６人,９州で６１の竜巻の竜巻が発生しました。これらは地球温暖化ガ原因だと考えられています。",
            countryJson = "usa",
            articleURL = "https://news.yahoo.co.jp/articles/48f640a21bb58a32f341f0f1e0271356402df3d3",
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
            country = "インド",
            title = "インド",
            message = "地球温暖化による大雨の被害で、多数の死者が出ています。",
            countryJson = "ind",
            articleURL = "https://news.yahoo.co.jp/articles/0d5f4a6ff501f17b0118ca98d9f64ac174c6a0b0",
            latitude = 20.5936,
            longitude = 78.9628,
            zoom = 4f,
        )
    )
    val level1size = level1Message.size
    val level2Message = listOf(
        MapDetailEntity(
            country = "グリーンランド",
            title = "グリーンランドについて",
            message = "グリーンランドは、2100年までに海面が10～18cm上昇するといわれています。",
            countryJson = "grl",
            articleURL = "https://news.yahoo.co.jp/articles/9e465cc3ae712e3e542a623392ca57b6730116c6",
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