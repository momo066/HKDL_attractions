package com.example.hkdlapp

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// アトラクションの状態を管理するクラス
class AttractionState(
    val order: Int,
    val name: String,
    val area: String,
    val waitMin: Int,
    val rideMin: Int
) {
    val totalMin get() = waitMin + rideMin
    var isChecked by mutableStateOf(false)
    var exitTime by mutableStateOf("")
}

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
    // 全25施設の初期データ（待ち時間・乗車時間は平均的な目安）
    val initialData = listOf(
        AttractionState(1, "フローズン・エバー・アフター", "ワールド・オブ・フローズン", 60, 5),
        AttractionState(2, "ワンダリング・オーケンズ・スライディング・スレイ", "ワールド・オブ・フローズン", 40, 2),
        AttractionState(3, "プレイハウス・イン・ザ・ウッズ", "ワールド・オブ・フローズン", 30, 15),
        AttractionState(4, "RCレーサー", "トイ・ストーリー・ランド", 30, 2),
        AttractionState(5, "スリンキー・ドッグ・スピン", "トイ・ストーリー・ランド", 20, 2),
        AttractionState(6, "トイ・ソルジャー・パラシュート・ドロップ", "トイ・ストーリー・ランド", 30, 3),
        AttractionState(7, "ミスティック・マナー", "ミスティック・ポイント", 15, 5),
        AttractionState(8, "ビッグ・グリズリー・マウンテン", "グリズリー・ガルチ", 20, 3),
        AttractionState(9, "ジャングル・リバー・クルーズ", "アドベンチャーランド", 20, 8),
        AttractionState(10, "ターザンのツリーハウス行きいかだ", "アドベンチャーランド", 10, 5),
        AttractionState(11, "ターザンのツリーハウス", "アドベンチャーランド", 0, 15),
        AttractionState(12, "アイアンマン・エクスペリエンス", "トゥモローランド", 20, 5),
        AttractionState(13, "アントマン＆ワスプ：ナノ・バトル！", "トゥモローランド", 15, 5),
        AttractionState(14, "ハイパースペース・マウンテン", "トゥモローランド", 25, 3),
        AttractionState(15, "オービトロン", "トゥモローランド", 20, 2),
        AttractionState(16, "プーさんの冒険", "ファンタジーランド", 30, 4),
        AttractionState(17, "イッツ・ア・スモールワールド", "ファンタジーランド", 10, 10),
        AttractionState(18, "ミッキーのフィルハーマジック", "ファンタジーランド", 15, 12),
        AttractionState(19, "空飛ぶダンボ", "ファンタジーランド", 25, 2),
        AttractionState(20, "シンデレラカルーセル", "ファンタジーランド", 15, 2),
        AttractionState(21, "マッドハッターのティーカップ", "ファンタジーランド", 15, 2),
        AttractionState(22, "フェアリーテイル・フォレスト", "ファンタジーランド", 0, 15),
        AttractionState(23, "香港ディズニーランド鉄道", "メインストリートUSA", 20, 20),
        AttractionState(24, "メインストリート・ヴィークル", "メインストリートUSA", 10, 10),
        AttractionState(25, "アニメーション・アカデミー", "メインストリートUSA", 20, 20)
    )

    val states = remember { initialData }
    var tabIndex by remember { mutableStateOf(0) }
    var currentMapUrl by remember { mutableStateOf("https://www.hongkongdisneyland.com/ja/maps/") }
    
    var startTime by remember { mutableStateOf("10:00") }
    var closeTime by remember { mutableStateOf("20:30") }

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
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (tabIndex == 0) {
                // 上部のステータス＆入力エリア
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("入園時間(例10:00)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = closeTime,
                            onValueChange = { closeTime = it },
                            label = { Text("閉園時間(例20:30)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    val completedCount = states.count { it.isChecked }
                    val finishEst = calculateFinishTime(states, startTime, closeTime)
                    
                    Text("進捗: $completedCount / ${states.size} 完了", fontWeight = FontWeight.Bold)
                    Text("制覇予想: $finishEst", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                // アトラクションリスト
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(states) { state ->
                        val bgColor = if (state.isChecked) Color(0xFFE8F5E9) else Color.White
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = state.isChecked,
                                        onCheckedChange = { state.isChecked = it }
                                    )
                                    Text("${state.order}.", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(state.name, fontWeight = FontWeight.Bold)
                                        Text("${state.area} | 待${state.waitMin}分+乗${state.rideMin}分 = 所要${state.totalMin}分", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = state.exitTime,
                                        onValueChange = { state.exitTime = it },
                                        label = { Text("降りた時間(例10:30)") },
                                        modifier = Modifier.weight(1f).height(56.dp),
                                        singleLine = true
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = {
                                        // Googleマップで直接検索結果のピンを表示するURL
                                        currentMapUrl = "https://www.google.com/maps/search/?api=1&query=${Uri.encode("Hong Kong Disneyland " + state.name)}"
                                        tabIndex = 1
                                    }) {
                                        Text("地図")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // マップ表示エリア
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
                                domStorageEnabled = true
                            }
                            loadUrl(currentMapUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { webView ->
                        if (webView.url != currentMapUrl) {
                            webView.loadUrl(currentMapUrl)
                        }
                    }
                )
            }
        }
    }
}

// 時間計算ロジック
fun parseTime(timeStr: String): Int {
    val parts = timeStr.replace("：", ":").split(":")
    if (parts.size >= 2) {
        val h = parts[0].trim().toIntOrNull() ?: 0
        val m = parts[1].trim().toIntOrNull() ?: 0
        return h * 60 + m
    }
    return 0
}

fun formatTime(mins: Int): String {
    val h = (mins / 60) % 24
    val m = mins % 60
    return "%02d:%02d".format(h, m)
}

fun calculateFinishTime(states: List<AttractionState>, startStr: String, closeStr: String): String {
    val startMins = parseTime(startStr)
    val closeMins = parseTime(closeStr)
    if (startMins == 0 || closeMins == 0 || closeMins <= startMins) return "時間形式エラー"

    val completed = states.filter { it.isChecked }
    val remaining = states.size - completed.size

    if (remaining == 0) return "制覇完了！"

    val currentMins = completed.map { parseTime(it.exitTime) }.maxOrNull()?.takeIf { it > startMins } ?: startMins
    
    // 現在のペース（1アトラクションあたりの実消費時間）、実績がない場合は想定平均値
    val pace = if (completed.isEmpty()) {
        states.sumOf { it.totalMin } / states.size
    } else {
        (currentMins - startMins) / completed.size
    }

    val remainingTimeNeeded = pace * remaining
    var estimatedMins = currentMins + remainingTimeNeeded
    var day = 1

    val minsLeftToday = closeMins - currentMins
    if (remainingTimeNeeded > minsLeftToday && minsLeftToday > 0) {
        var carryOver = remainingTimeNeeded - minsLeftToday
        val dayLength = closeMins - startMins
        while (carryOver > dayLength) {
            day++
            carryOver -= dayLength
        }
        estimatedMins = startMins + carryOver
    } else if (minsLeftToday <= 0) {
        var carryOver = remainingTimeNeeded
        val dayLength = closeMins - startMins
        while (carryOver > dayLength) {
            day++
            carryOver -= dayLength
        }
        day++
        estimatedMins = startMins + carryOver
    }

    return "${day}日目 ${formatTime(estimatedMins)}"
}
