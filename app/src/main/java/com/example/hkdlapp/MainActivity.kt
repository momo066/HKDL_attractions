package com.example.hkdlapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material.icons.filled.Star
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

// 永続化（保存）対応の状態管理クラス
class ItemState(
    val id: String, // 保存用のユニークキー
    val order: Int,
    val name: String,
    val area: String,
    val waitMin: Int,
    val rideMin: Int,
    val urlSlug: String,
    val isShow: Boolean, // アトラクションかショーかの判別
    private val prefs: SharedPreferences
) {
    val totalMin get() = waitMin + rideMin
    
    // アプリ起動時に保存されたデータを読み込む
    var isChecked by mutableStateOf(prefs.getBoolean("${id}_checked", false))
    var exitHour by mutableStateOf(if (prefs.contains("${id}_exitHour")) prefs.getInt("${id}_exitHour", 0) else null)
    var exitMinute by mutableStateOf(if (prefs.contains("${id}_exitMinute")) prefs.getInt("${id}_exitMinute", 0) else null)

    // チェック状態を保存する関数
    fun updateChecked(checked: Boolean) {
        isChecked = checked
        prefs.edit().putBoolean("${id}_checked", checked).apply()
    }

    // 時間を保存する関数
    fun updateExitTime(h: Int?, m: Int?) {
        exitHour = h
        exitMinute = m
        val editor = prefs.edit()
        if (h != null) editor.putInt("${id}_exitHour", h) else editor.remove("${id}_exitHour")
        if (m != null) editor.putInt("${id}_exitMinute", m) else editor.remove("${id}_exitMinute")
        editor.apply()
    }
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
    val context = LocalContext.current
    // データを保存するためのSharedPreferences
    val prefs = remember { context.getSharedPreferences("HKDL_PREFS_2026", Context.MODE_PRIVATE) }

    val allItems = remember {
        listOf(
            // --- アトラクション ---
            ItemState("att_1", 1, "フローズン・エバー・アフター", "ワールド・オブ・フローズン", 60, 5, "frozen-ever-after", false, prefs),
            ItemState("att_2", 2, "ワンダリング・オーケンズ・スライディング・スレイ", "ワールド・オブ・フローズン", 40, 2, "wandering-oaken-sliding-sleighs", false, prefs),
            ItemState("att_3", 3, "プレイハウス・イン・ザ・ウッズ", "ワールド・オブ・フローズン", 30, 15, "playhouse-in-the-woods", false, prefs),
            ItemState("att_4", 4, "RCレーサー", "トイ・ストーリー・ランド", 30, 2, "rc-racer", false, prefs),
            ItemState("att_5", 5, "スリンキー・ドッグ・スピン", "トイ・ストーリー・ランド", 20, 2, "slinky-dog-spin", false, prefs),
            ItemState("att_6", 6, "トイ・ソルジャー・パラシュート・ドロップ", "トイ・ストーリー・ランド", 30, 3, "toy-soldier-parachute-drop", false, prefs),
            ItemState("att_7", 7, "ミスティック・マナー", "ミスティック・ポイント", 15, 5, "mystic-manor", false, prefs),
            ItemState("att_8", 8, "ビッグ・グリズリー・マウンテン", "グリズリー・ガルチ", 20, 3, "big-grizzly-mountain-runaway-mine-cars", false, prefs),
            ItemState("att_9", 9, "ジャングル・リバー・クルーズ", "アドベンチャーランド", 20, 8, "jungle-river-cruise", false, prefs),
            ItemState("att_10", 10, "ターザンのツリーハウス行きいかだ", "アドベンチャーランド", 10, 5, "rafts-to-tarzans-treehouse", false, prefs),
            ItemState("att_11", 11, "ターザンのツリーハウス", "アドベンチャーランド", 0, 15, "tarzans-treehouse", false, prefs),
            ItemState("att_12", 12, "アイアンマン・エクスペリエンス", "トゥモローランド", 20, 5, "iron-man-experience", false, prefs),
            ItemState("att_13", 13, "アントマン＆ワスプ：ナノ・バトル！", "トゥモローランド", 15, 5, "ant-man-and-the-wasp-nano-battle", false, prefs),
            ItemState("att_14", 14, "ハイパースペース・マウンテン", "トゥモローランド", 25, 3, "hyperspace-mountain", false, prefs),
            ItemState("att_15", 15, "オービトロン", "トゥモローランド", 20, 2, "orbitron", false, prefs),
            ItemState("att_16", 16, "プーさんの冒険", "ファンタジーランド", 30, 4, "many-adventures-of-winnie-the-pooh", false, prefs),
            ItemState("att_17", 17, "イッツ・ア・スモールワールド", "ファンタジーランド", 10, 10, "its-a-small-world", false, prefs),
            ItemState("att_18", 18, "ミッキーのフィルハーマジック", "ファンタジーランド", 15, 12, "mickeys-philharmagic", false, prefs),
            ItemState("att_19", 19, "空飛ぶダンボ", "ファンタジーランド", 25, 2, "dumbo-the-flying-elephant", false, prefs),
            ItemState("att_20", 20, "シンデレラカルーセル", "ファンタジーランド", 15, 2, "cinderella-carousel", false, prefs),
            ItemState("att_21", 21, "マッドハッターのティーカップ", "ファンタジーランド", 15, 2, "mad-hatter-tea-cups", false, prefs),
            ItemState("att_22", 22, "フェアリーテイル・フォレスト", "ファンタジーランド", 0, 15, "fairy-tale-forest", false, prefs),
            ItemState("att_23", 23, "香港ディズニーランド鉄道", "メインストリートUSA", 20, 20, "hong-kong-disneyland-railroad", false, prefs),
            ItemState("att_24", 24, "メインストリート・ヴィークル", "メインストリートUSA", 10, 10, "main-street-vehicles", false, prefs),
            ItemState("att_25", 25, "アニメーション・アカデミー", "メインストリートUSA", 20, 20, "animation-academy", false, prefs),
            
            // --- ショー（2026年最新版） ---
            ItemState("show_1", 1, "フレンドタスティック！ パレード", "メインストリートUSA", 30, 30, "friendtastic-parade", true, prefs),
            ItemState("show_2", 2, "モーメンタス（20周年特別版）", "メインストリートUSA", 45, 20, "momentous-party-in-the-night-sky", true, prefs),
            ItemState("show_3", 3, "ミッキー・アンド・ザ・ワンダラス・ブック", "ファンタジーランド", 45, 25, "mickeys-wondrous-book", true, prefs),
            ItemState("show_4", 4, "フェスティバル・オブ・ザ・ライオンキング", "アドベンチャーランド", 30, 30, "festival-of-the-lion-king", true, prefs),
            ItemState("show_5", 5, "ディズニー・フレンズ・ライブ：キャッスル・パーティー", "メインストリートUSA", 30, 20, "castle-party", true, prefs),
            ItemState("show_6", 6, "モアナ：ホームカミング・セレブレーション", "アドベンチャーランド", 20, 25, "moana-a-homecoming-celebration", true, prefs)
        )
    }

    val attractions = allItems.filter { !it.isShow }
    val shows = allItems.filter { it.isShow }
    
    // タブ（0:アトラクション, 1:ショー, 2:マップ）
    var tabIndex by remember { mutableStateOf(prefs.getInt("lastTabIndex", 0)) }
    
    // 時間設定の保存対応
    var startHour by remember { mutableStateOf(prefs.getInt("startHour", 10)) }
    var startMinute by remember { mutableStateOf(prefs.getInt("startMinute", 0)) }
    var closeHour by remember { mutableStateOf(prefs.getInt("closeHour", 20)) }
    var closeMinute by remember { mutableStateOf(prefs.getInt("closeMinute", 30)) }

    fun saveTimeSetting(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    // WebView設定
    var currentUrl by remember { mutableStateOf("https://www.hongkongdisneyland.com/ja/attractions/") }
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: ""
                    if (url.startsWith("http://") || url.startsWith("https://")) return false 
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

    BackHandler(enabled = tabIndex == 2) {
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
                    onClick = { tabIndex = 0; prefs.edit().putInt("lastTabIndex", 0).apply() },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("ルート") }
                )
                NavigationBarItem(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1; prefs.edit().putInt("lastTabIndex", 1).apply() },
                    icon = { Icon(Icons.Default.Star, null) },
                    label = { Text("ショー") }
                )
                NavigationBarItem(
                    selected = tabIndex == 2,
                    onClick = { tabIndex = 2; prefs.edit().putInt("lastTabIndex", 2).apply() },
                    icon = { Icon(Icons.Default.Place, null) },
                    label = { Text("マップ") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // 全タブ共通のヘッダー情報（マップタブ以外で表示）
            if (tabIndex != 2) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("入園:", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(startHour, (9..21).toList(), { startHour = it; saveTimeSetting("startHour", it) }, "%d")
                        Text(":", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(startMinute, (0..55 step 5).toList(), { startMinute = it; saveTimeSetting("startMinute", it) }, "%02d")
                        
                        Spacer(Modifier.width(8.dp))
                        
                        Text("閉園:", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(closeHour, (9..23).toList(), { closeHour = it; saveTimeSetting("closeHour", it) }, "%d")
                        Text(":", modifier = Modifier.align(Alignment.CenterVertically))
                        TimeDropdown(closeMinute, (0..55 step 5).toList(), { closeMinute = it; saveTimeSetting("closeMinute", it) }, "%02d")
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    val completedCount = allItems.count { it.isChecked }
                    val finishEst = calculatePaceAndFinish(allItems, startHour, startMinute, closeHour, closeMinute)
                    
                    Text("全体進捗: $completedCount / ${allItems.size} 完了", fontWeight = FontWeight.Bold)
                    Text("全制覇予想: $finishEst", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // アトラクションタブ (tabIndex == 0)
                Column(modifier = Modifier.fillMaxSize().graphicsLayer {
                    alpha = if (tabIndex == 0) 1f else 0f
                    translationX = if (tabIndex == 0) 0f else 10000f
                }) {
                    ItemListView(attractions, webView) { tabIndex = 2 }
                }

                // ショータブ (tabIndex == 1)
                Column(modifier = Modifier.fillMaxSize().graphicsLayer {
                    alpha = if (tabIndex == 1) 1f else 0f
                    translationX = if (tabIndex == 1) 0f else 10000f
                }) {
                    ItemListView(shows, webView) { tabIndex = 2 }
                }

                // マップタブ (tabIndex == 2)
                Column(modifier = Modifier.fillMaxSize().graphicsLayer {
                    alpha = if (tabIndex == 2) 1f else 0f
                    translationX = if (tabIndex == 2) 0f else 10000f
                }) {
                    Surface(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                        SelectionContainer {
                            Text(text = currentUrl, modifier = Modifier.padding(12.dp), maxLines = 1, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    AndroidView(factory = { webView }, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/attractions/") }) { Text("ｱﾄﾗｸｼｮﾝ", style = MaterialTheme.typography.bodySmall) }
                        Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/entertainment/#/live-entertainment/sort=alpha/") }) { Text("ライブ", style = MaterialTheme.typography.bodySmall) }
                        Button(onClick = { webView.loadUrl("https://www.hongkongdisneyland.com/ja/dining/#/sort=alpha/") }) { Text("ﾀﾞｲﾆﾝｸﾞ", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}

// リスト表示用コンポーネント
@Composable
fun ItemListView(items: List<ItemState>, webView: WebView, onNavigateMap: () -> Unit) {
    LazyColumn {
        items(items) { state ->
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
                            onCheckedChange = { state.updateChecked(it) }
                        )
                        Text("${state.order}.", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(state.name, fontWeight = FontWeight.Bold)
                            val waitText = if (state.isShow) "待機" else "待"
                            val rideText = if (state.isShow) "公演" else "乗"
                            Text("${state.area} | $waitText${state.waitMin}分+$rideText${state.rideMin}分 = 所要${state.totalMin}分", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val endText = if (state.isShow) "終了:" else "降車:"
                        Text(endText, modifier = Modifier.padding(end = 4.dp))
                        TimeDropdown(state.exitHour, (9..23).toList(), { state.updateExitTime(it, state.exitMinute) }, "%d")
                        Text(" : ", modifier = Modifier.padding(horizontal = 4.dp))
                        TimeDropdown(state.exitMinute, (0..55 step 5).toList(), { state.updateExitTime(state.exitHour, it) }, "%02d")
                        
                        Spacer(Modifier.weight(1f))
                        
                        Button(onClick = {
                            val category = if (state.isShow) "entertainment" else "attractions"
                            webView.loadUrl("https://www.hongkongdisneyland.com/ja/$category/${state.urlSlug}/")
                            onNavigateMap()
                        }) {
                            Text("詳細(地図)")
                        }
                    }
                }
            }
        }
    }
}

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
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}

fun calculatePaceAndFinish(
    states: List<ItemState>,
    startH: Int, startM: Int,
    closeH: Int, closeM: Int
): String {
    val startMins = startH * 60 + startM
    val closeMins = closeH * 60 + closeM
    if (startMins >= closeMins) return "時間設定エラー"

    val checked = states.filter { it.isChecked }
    val remaining = states.filter { !it.isChecked }

    if (remaining.isEmpty()) return "制覇完了！"

    var currentMins = startMins
    for (item in checked) {
        if (item.exitHour != null && item.exitMinute != null) {
            val exit = item.exitHour!! * 60 + item.exitMinute!!
            if (exit > currentMins) currentMins = exit
        }
    }

    var currentDay = 1
    for (item in remaining) {
        if (currentMins + item.totalMin > closeMins) {
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
