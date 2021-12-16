package com.example.stairtree.ui.map.detail

object MapDetailObject {
    val level1Message = listOf(
        MapDetailEntity(
            country = "アメリカ",
            title = "アメリカで発生した地球温暖化の事例",
            message = "アメリカのフロリダ半島に巨大が上陸、その後ルイジアナ州に再上陸、大きな被害をもたらしました。",
            countryJson = "usa",
            articleURL = "https://www.bbc.com/japanese/50384396",
            latitude = 37.0902,
            longitude = -100.7128,
            zoom = 3f,
        ),
        MapDetailEntity(
            country = "ツバル",
            title = "ツバル",
            message = "",
            countryJson = "tuv",
            articleURL = "https://www3.nhk.or.jp/news/html/20211110/k10013341181000.html",
            latitude = -8.516667,
            longitude = 179.216667,
            zoom = 8f,
        ),
        MapDetailEntity(
            country = "バングラデシュ",
            title = "バングラデシュ",
            message = "地球温暖化によるサイクロンによって、多くの死者が出ています",
            countryJson = "bgd",
            articleURL = "https://www.unicef.or.jp/news/2019/0057.html",
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
            message = " グリーンランドは、2100年までに海面が60cm上昇するといわれています。",
            countryJson = "grl",
            articleURL = "https://style.nikkei.com/article/DGXMZO64709280X01C20A0000000/",
            latitude = 71.7069,
            longitude = -42.6043,
            zoom = 3f,
        ),
        MapDetailEntity(
            country = "日本",
            title = "日本について",
            message = "日本は100年後スーパー台風ってのがめっちゃ増えます",
            countryJson = "jpn",
            articleURL = "https://www.nikkei.com/article/DGXNASDG29034_Z20C12A5CR8000/",
            latitude = 33.0,
            longitude = 138.0,
            zoom = 4f,
        )
    )
    val level2size = level2Message.size
}