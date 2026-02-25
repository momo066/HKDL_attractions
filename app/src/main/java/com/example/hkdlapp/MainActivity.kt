package com.example.hkdlapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// アトラクションの状態を管理するクラス
class AttractionState(
    val order: Int,
    val name: String,
    val area: String,
    val waitMin: Int,
    val rideMin: Int,
    val urlSlug: String // 公式サイトのURL末尾
) {
    val totalMin get() = waitMin + rideMin
    var isChecked by mutableStateOf(false)
    var exitHour by mutableStateOf<Int?>(null)
    var exitMinute by mutableStateOf<Int?>(null)
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HKDLApp() {
    val initialData = listOf(
        AttractionState(1, "フローズン・エバー・アフター", "ワールド・オブ・フローズン", 60, 5, "frozen-ever-after"),
        AttractionState(2, "ワンダリング・オーケンズ・スライディング・スレイ", "ワールド・オブ・フローズン", 40, 2, "wandering-oaken-sliding-sleighs"),
        AttractionState(3, "プレイハウス・イン・ザ・ウッズ", "ワールド・オブ・フローズン", 30, 15, "playhouse-in-the-woods"),
        AttractionState(4, "RCレーサー", "トイ・ストーリー・ランド", 30, 2, "rc-racer"),
        AttractionState(5, "スリンキー・ドッグ・スピン", "トイ・ストーリー・ランド", 20, 2, "slinky-dog-spin"),
        AttractionState(6, "トイ・ソルジャー・パラシュート・ドロップ", "トイ・ストーリー・ランド", 30, 3, "toy-soldier-parachute-drop"),
        AttractionState(7, "ミスティック・マナー", "ミスティック・ポイント", 15, 5, "mystic-manor"),
        AttractionState(8, "ビッグ・グリズリー・マウンテン", "グリズリー・ガルチ", 20, 3, "big-grizzly-mountain-runaway-mine-cars"),
        AttractionState(9, "ジャングル・リバー・クルーズ", "アドベンチャーランド", 20, 8, "jungle-river-cruise"),
        AttractionState(10, "ターザンのツリーハウス行きいかだ", "アドベンチャーランド", 10, 5, "rafts-to-tarzans-treehouse"),
        AttractionState(11, "ターザンのツリーハウス", "アドベンチャーランド", 0, 15, "tarzans-treehouse"),
        AttractionState(12, "アイアンマン・エクスペリエンス", "トゥモローランド", 20, 5, "iron-man-experience"),
        AttractionState(13, "アントマン＆ワスプ：ナノ・バトル！", "トゥモローランド", 15, 5, "ant-man-and-the-wasp-nano-battle"),
        AttractionState(14, "ハイパースペース・マウンテン", "トゥモローランド", 25, 3, "hyperspace-mountain"),
        AttractionState(15, "オービトロン", "トゥモローランド", 20, 2, "orbitron"),
        AttractionState(16, "プーさんの冒険", "ファンタジーランド", 30, 4, "many-adventures-of-winnie-the-pooh"),
        AttractionState(17, "イッツ・ア・スモールワールド", "ファンタジーランド", 10, 10, "its-a-small-world"),
        AttractionState(18, "ミッキーのフィルハーマジック", "ファンタジーランド", 15, 12, "mickeys-philharmagic"),
        AttractionState(19, "空飛ぶダンボ", "ファンタジーランド", 25, 2, "dumbo-the-flying-elephant"),
        AttractionState(20, "シンデレラカルーセル", "ファンタジーランド", 15, 2, "cinderella-carousel"),
        AttractionState(21, "マッドハッターのティーカップ", "ファンタジーランド", 15, 2, "mad-hatter-tea-cups"),
        AttractionState(22, "フェアリーテイル・フォレスト", "ファンタジーランド", 0, 15, "fairy-tale-forest"),
        AttractionState(23, "香港ディズニーランド鉄道", "メインストリートUSA", 20, 20, "hong-kong-disneyland-railroad"),
        AttractionState(24, "メインストリート・ヴィークル", "メインストリートUSA", 10, 10, "main-street-vehicles"),
        AttractionState(25, "アニメーション・アカデミー", "メインストリートUSA", 20, 20, "animation-academy")
    )

    val states = remember { initialData }
    var tabIndex by remember { mutableStateOf(0) }
    
    // 時間設定の初期値
    var startHour by remember { mutableStateOf(10) }
    var startMinute by remember { mutableStateOf(0) }
    var closeHour by remember { mutableStateOf(20) }
    var closeMinute by remember { mutableStateOf(30) }

    // WebViewのインスタンスを保持し、再ロードを防ぐ
    val context = LocalContext.current
    var currentUrl by remember { mutableStateOf("https://www.hongkongdisneyland.com/ja/attractions/") }
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: ""
                    // intent:// 等のエラーを防ぐため、http(s)のみ許可
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        return false 
                    }
                    return true 
                }
                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    currentUrl = url ?: ""
                }
            }
            loadUrl("https://www.hongkongdisneyland.com/ja/attractions/")
        }
    }

    // 戻るボタンの制御（マップタブを開いている時だけWebViewを戻る）
    BackHandler(enabled = tabIndex == 1) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            tabIndex = 0
        }
    }

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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // ---------------------------
            // ルートタブ (tabIndex == 0)
            // ---------------------------
            Column(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = if (tabIndex == 0) 1f else 0f
                    translationX = if (tabIndex == 0) 0f else 10000f
                }
            ) {
                // 上部のステータス＆入力エリア
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("入園時間:", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(startHour, (9..21).toList(), { startHour = it }, "%d")
                        Text(":", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(startMinute, (0..55 step 5).toList(), { startMinute = it }, "%02d")
                        
                        Spacer(Modifier.width(8.dp))
                        
                        Text("閉園時間:", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(closeHour, (9..23).toList(), { closeHour = it }, "%d")
                        Text(":", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(closeMinute, (0..55 step 5).toList(), { closeMinute = it }, "%02d")
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    val completedCount = states.count { it.isChecked }
                    val finishEst = calculatePaceAndFinish(states, startHour, startMinute, closeHour, closeMinute)
                    
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
                                    Text("降車:", modifier = Modifier.padding(end = 4.dp))
                                    TimeDropdown(state.exitHour, (9..23).toList(), { state.exitHour = it }, "%d")
                                    Text(" : ", modifier = Modifier.padding(horizontal = 4.dp))
                                    TimeDropdown(state.exitMinute, (0..55 step 5).toList(), { state.exitMinute = it }, "%02d")
                                    
                                    Spacer(Modifier.weight(1f))
                                    
                                    Button(onClick = {
                                        val targetUrl = "https://www.hongkongdisneyland.com/ja/attractions/${state.urlSlug}/"
                                        webView.loadUrl(targetUrl)
                                        tabIndex = 1
                                    }) {
                                        Text("詳細(地図)")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ---------------------------
            // マップタブ (tabIndex == 1)
            // ---------------------------
            Column(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // タブ切り替え時にWebViewが破棄されるのを防ぐため、透明度と位置で隠す
                    alpha = if (tabIndex == 1) 1f else 0f
                    translationX = if (tabIndex == 1) 0f else 10000f
                }
            ) {
                // 上部URLバー（コピペ用）
                Surface(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                    SelectionContainer {
                        Text(
                            text = currentUrl,
                            modifier = Modifier.padding(12.dp),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // WebView本体
                AndroidView(
                    factory = { webView },
                    modifier = Modifier.weight(1f)
                )

                // 下部ショートカットボタン
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                ) {
                    Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/attractions/") }) {
                        Text("ｱﾄﾗｸｼｮﾝ", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/entertainment/#/live-entertainment/sort=alpha/") }) {
                        Text("ライブ", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/dining/#/sort=alpha/") }) {
                        Text("ﾀﾞｲﾆﾝｸﾞ", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// ドロップダウンピッカー用コンポーネント
@Composable
fun TimeDropdown(value: Int?, options: List<Int>, onSelect: (Int) -> Unit, format: String) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            modifier = Modifier.defaultMinSize(minWidth = 50.dp, minHeight = 36.dp)
        ) {
            Text(if (value != null) String.format(format, value) else "--")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(String.format(format, opt)) },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 正確な到着・制覇時間の計算ロジック
fun calculatePaceAndFinish(
    states: List<AttractionState>,
    startH: Int, startM: Int,
    closeH: Int, closeM: Int
): String {
    val startMins = startH * 60 + startM
    val closeMins = closeH * 60 + closeM
    if (startMins >= closeMins) return "時間設定エラー"

    val checked = states.filter { it.isChecked }
    val remaining = states.filter { !it.isChecked }

    if (remaining.isEmpty()) return "制覇完了！"

    // 現在時刻（入力された一番遅い降車時間、もしくは入園時間）
    var currentMins = startMins
    for (item in checked) {
        if (item.exitHour != null && item.exitMinute != null) {
            val exit = item.exitHour!! * 60 + item.exitMinute!!
            if (exit > currentMins) {
                currentMins = exit
            }
        }
    }

    var currentDay = 1
    // 残りのアトラクションの所要時間を足していく
    for (item in remaining) {
        if (currentMins + item.totalMin > closeMins) {
            // 閉園時間を超える場合は次の日の朝に繰り越し
            currentDay++
            currentMins = startMins + item.totalMin
        } else {
            currentMins += item.totalMin
        }
    }

    val fh = (currentMins / 60) % 24
    val fm = currentMins % 60
    return "${currentDay}日目 %02d:%02d".format(fh, fm)
}
