//package com.example.downtimeguard.ui.theme.dashboards
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import com.example.downtimeguard.ui.theme.MainScreen
//import com.example.downtimeguard.ui.theme.ui.theme.DowntimeGuardTheme
//import kotlinx.coroutines.delay
//
//// ----- Model -----
//data class AppCard(
//    val name: String,
//    val minutes: Int,
//    val icon: ImageBitmap,       // create with drawable.toBitmap().asImageBitmap()
//    val packageName: String? = null
//)
//
//class Carousel : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
////            DowntimeGuardTheme {
////                MainScreen(
////
//////                        name = "Android",
////                        modifier = Modifier.padding(innerPadding)
//                )
//            }
//        }
//    }
//}
//
//// ----- Carousel -----
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun Carousel(
//    apps: List<AppCard>,
//    modifier: Modifier = Modifier,
//    autoScrollMs: Long = 3500L,
//    onAppClick: (AppCard) -> Unit = {}
//) {
//    if (apps.isEmpty()) {
//        Box(
//            modifier
//                .fillMaxWidth()
//                .height(220.dp)
//                .clip(RoundedCornerShape(24.dp))
//                .background(MaterialTheme.colorScheme.surfaceVariant),
//            contentAlignment = Alignment.Center
//        ) { Text("No data yet") }
//        return
//    }
//
//    val total = remember(apps) { apps.sumOf { it.minutes } }
//    val pagerState = rememberPagerState { apps.size }
//
//    // Auto-scroll
//    LaunchedEffect(pagerState.currentPage, apps.size) {
//        if (apps.size > 1) {
//            delay(autoScrollMs)
//            val next = (pagerState.currentPage + 1) % apps.size
//            pagerState.animateScrollToPage(next)
//        }
//    }
//
//    Column(modifier = modifier.fillMaxWidth()) {
//        HorizontalPager(
//            state = pagerState,
//            beyondBoundsPageCount = 1,
//            pageSpacing = 16.dp,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(220.dp)
//        ) { page ->
//            val item = apps[page]
//            AppCardView(
//                item = item,
//                percent = if (total == 0) 0f else item.minutes.toFloat() / total,
//                onClick = { onAppClick(item) },
//                modifier = Modifier
//                    .padding(horizontal = 8.dp)
//                    .fillMaxWidth()
//            )
//        }
//
//        // Dots
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 12.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            repeat(apps.size) { i ->
//                val selected = i == pagerState.currentPage
//                Box(
//                    Modifier
//                        .padding(4.dp)
//                        .size(if (selected) 10.dp else 8.dp)
//                        .clip(CircleShape)
//                        .background(
//                            if (selected) MaterialTheme.colorScheme.primary
//                            else MaterialTheme.colorScheme.outlineVariant
//                        )
//                )
//            }
//        }
//    }
//}
//
//// ----- Card UI -----
//@Composable
//private fun AppCardView(
//    item: AppCard,
//    percent: Float,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val gradient = Brush.linearGradient(
//        listOf(
//            MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
//            MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)
//        ),
//        start = Offset.Zero,
//        end = Offset.Infinite
//    )
//
//    Card(
//        onClick = onClick,
//        modifier = modifier
//            .height(220.dp)
//            .clip(RoundedCornerShape(28.dp)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
//    ) {
//        Box(
//            Modifier
//                .background(gradient)
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            Row(
//                Modifier.fillMaxSize(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Icon bubble
//                Box(
//                    Modifier
//                        .size(72.dp)
//                        .clip(RoundedCornerShape(20.dp))
//                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
//                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        bitmap = item.icon,
//                        contentDescription = item.name,
//                        modifier = Modifier.size(48.dp),
//                        contentScale = ContentScale.Fit
//                    )
//                }
//
//                Spacer(Modifier.width(16.dp))
//
//                Column(Modifier.weight(1f)) {
//                    Text(item.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
//                    Text("${item.minutes} min", style = MaterialTheme.typography.bodyMedium)
//
//                    Spacer(Modifier.height(12.dp))
//
//                    // tiny usage bar
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .height(8.dp)
//                            .clip(RoundedCornerShape(999.dp))
//                            .background(MaterialTheme.colorScheme.surfaceVariant)
//                    ) {
//                        Box(
//                            Modifier
//                                .fillMaxWidth(fraction = percent.coerceIn(0f, 1f))
//                                .fillMaxHeight()
//                                .clip(RoundedCornerShape(999.dp))
//                                .background(MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//
//            // top-right chip (optional)
//            Box(
//                Modifier
//                    .align(Alignment.TopEnd)
//                    .clip(RoundedCornerShape(999.dp))
//                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
//                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(999.dp))
//                    .padding(horizontal = 10.dp, vertical = 6.dp)
//            ) {
//                Text("Tap for details", style = MaterialTheme.typography.labelMedium)
//            }
//        }
//    }
//}
