package com.example.hkdlapp

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

data class Attraction(val id: Int, val name: String, val area: String, val tip: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HKDLApp()
                }
            }
        }
    }
}

@Composable
fun HKDLApp() {
    val attractions = listOf(
        Attraction(1, "フローズン・エバー・アフター", "ワールド・オブ・フローズン", "ボートでアナ雪の世界へ。朝イチ推奨！"),
        Attraction(2, "ワンダリング・オーケンズ・スライディング・スレイ", "ワールド・オブ・フローズン", "ソリ型のミニコースター。"),
        Attraction(3, "プレイハウス・イン・ザ・ウッズ", "ワールド・オブ・フローズン", "アナやエルサに会える体験型施設。"),
        Attraction(4, "RCレーサー", "トイ・ストーリー・ランド", "U字型トラックを駆け抜ける絶叫系。"),
        Attraction(5, "スリンキー・ドッグ・スピン", "トイ・ストーリー・ランド", "犬のスリンキーがぐるぐる回るライド。"),
        Attraction(6, "トイ・ソルジャー・パラシュート・ドロップ", "トイ・ストーリー・ランド", "パラシュートで急降下するスリル満点ライド。"),
        Attraction(7, "ミスティック・マナー", "ミスティック・ポイント", "香港限定の進化版ホーンテッドマンション。"),
        Attraction(8, "ビッグ・グリズリー・マウンテン・ラナウェイ・マイン・カー", "グリズリー・ガルチ", "途中で逆走するスリル満点コースター。"),
        Attraction(9, "ジャングル・リバー・クルーズ", "アドベンチャーランド", "火と水を使った迫力満点のクルーズ。"),
        Attraction(10, "ターザンのツリーハウス行きいかだ", "アドベンチャーランド", "いかだに乗ってターザンの島へ。"),
        Attraction(11, "ターザンのツリーハウス", "アドベンチャーランド", "島を歩いて探検するウォークスルー型。"),
        Attraction(12, "アイアンマン・エクスペリエンス", "トゥモローランド", "香港限定！アイアンマンと一緒に空を飛ぶ。"),
        Attraction(13, "アントマン＆ワスプ：ナノ・バトル！", "トゥモローランド", "小さくなって戦うシューティングライド。"),
        Attraction(14, "ハイパースペース・マウンテン", "トゥモローランド", "スター・ウォーズ仕様の屋内コースター。"),
        Attraction(15, "オービトロン", "トゥモローランド", "空飛ぶ円盤を自分で操縦。夜景が綺麗。"),
        Attraction(16, "プーさんの冒険", "ファンタジーランド", "ハニーポットに乗ってプーさんの世界へ。"),
        Attraction(17, "イッツ・ア・スモールワールド", "ファンタジーランド", "ディズニーキャラクターも登場する世界旅行。"),
        Attraction(18, "ミッキーのフィルハーマジック", "ファンタジーランド", "ドナルドが巻き起こす3Dシアター。"),
        Attraction(19, "空飛ぶダンボ", "ファンタジーランド", "ダンボに乗って空を飛ぶ定番ライド。"),
        Attraction(20, "シンデレラカルーセル", "ファンタジーランド", "美しいメリーゴーランド。写真映え抜群。"),
        Attraction(21, "マッドハッターのティーカップ", "ファンタジーランド", "アリスの世界のコーヒーカップ。"),
        Attraction(22, "フェアリーテイル・フォレスト", "ファンタジーランド", "ミニチュアのディズニープリンセスの世界を散策。"),
        Attraction(23, "香港ディズニーランド鉄道", "メインストリートUSA", "パークをのんびり一周する蒸気機関車。"),
        Attraction(24, "メインストリート・ヴィークル", "メインストリートUSA", "レトロな車でメインストリートをドライブ。"),
        Attraction(25, "アニメーション・アカデミー", "メインストリートUSA", "ディズニーキャラクターの描き方を学べる。")
    )
    
    var tabIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("ルート") }
                )
                NavigationBarItem(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    icon = { Icon(Icons.Default.Place, null) },
                    label = { Text("マップ") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (tabIndex == 0) {
                LazyColumn {
                    items(attractions) { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = { Text("${item.area} | ${item.tip}") },
                            leadingContent = { Text(item.id.toString(), style = MaterialTheme.typography.headlineSmall) }
                        )
                        Divider()
                    }
                }
            } else {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true // この1行がマップ表示に必須
                            }
                            loadUrl("https://www.hongkongdisneyland.com/ja/maps/")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
