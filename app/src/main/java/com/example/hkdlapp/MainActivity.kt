package com.example.hkdlapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Attraction(val id: Int, val name: String, val area: String, val tip: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HKDLApp() }
    }
}

@Composable
fun HKDLApp() {
    val attractions = listOf(
        Attraction(1, "フローズン・エバー・アフター", "ワールド・オブ・フローズン", "朝イチ推奨！"),
        Attraction(2, "オーケン・スライディング・スレイ", "ワールド・オブ・フローズン", "続けて乗車。"),
        Attraction(3, "RCレーサー", "トイ・ストーリー", "ここから反時計回りに。"),
        Attraction(4, "ミスティック・マナー", "ミスティック・ポイント", "香港限定！"),
        Attraction(5, "ビッグ・グリズリー・マウンテン", "グリズリー・ガルチ", "逆走コースター。"),
        Attraction(6, "ジャングル・リバー・クルーズ", "アドベンチャー", "昼間がおすすめ。"),
        Attraction(7, "プーさんのハニーハント", "ファンタジー", "夕方が空きます。"),
        Attraction(8, "イッツ・スモールワールド", "ファンタジー", "待ち時間ほぼ無し。"),
        Attraction(9, "ハイパースペース・マウンテン", "トゥモロー", "屋内コースター。"),
        Attraction(10, "アイアンマン / アントマン", "トゥモロー", "マーベルで締め！")
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.List, "List") }, label = { Text("ルート") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.Map, "Map") }, label = { Text("マップ") })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (selectedTab == 0) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(attractions) { attraction ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${attraction.id}", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(attraction.name, style = MaterialTheme.typography.titleLarge)
                                    Text(attraction.area, color = Color.Gray)
                                    Text(attraction.tip, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("反時計回りに進んでください\n(入口 → 右奥 → 左奥)", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
