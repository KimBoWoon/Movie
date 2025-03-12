//package com.bowoon.ui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ExperimentalLayoutApi
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import com.bowoon.model.Favorite
//import com.bowoon.model.Week
//import org.threeten.bp.LocalDate
//import org.threeten.bp.format.DateTimeFormatter
//
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//fun Calendar(
//    today: LocalDate,
//    favoriteMovies: List<Favorite>,
//    onMovieClick: (Int) -> Unit,
//    scrollEvent: () -> Unit
//) {
//    var releaseDate by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text(
//            modifier = Modifier.fillMaxWidth(),
//            text = today.format(DateTimeFormatter.ofPattern("yyyy년 MM월")),
//            fontSize = sp15,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
//        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Week.entries.forEach {
//                Text(
//                    modifier = Modifier.weight(1f),
//                    text = it.label,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//
//        FlowRow(
//            modifier = Modifier.fillMaxWidth(),
//            maxLines = 5,
//            maxItemsInEachRow = 7
//        ) {
//            val firstDay = today.withDayOfMonth(1)
//
//            for (i in 0 until (firstDay.dayOfWeek.value % 7)) {
//                Spacer(modifier = Modifier
//                    .height(dp30)
//                    .weight(1f)
//                    .background(color = Color.Transparent))
//            }
//
//            for (day in 1 until today.lengthOfMonth() + 1) {
//                if (favoriteMovies.find { it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate != null && LocalDate.parse(it.releases?.countries?.find { it.iso31661.equals("KR", true) }?.releaseDate) == today.withDayOfMonth(day) } != null) {
//                    Text(
//                        modifier = Modifier
//                            .height(dp30)
//                            .weight(1f)
//                            .background(color = Color.Yellow, shape = RoundedCornerShape(dp10))
//                            .bounceClick {
//                                scrollEvent()
//                                releaseDate = today
//                                    .withDayOfMonth(day)
//                                    .toString()
//                            },
//                        text = "$day",
//                        textAlign = TextAlign.Center,
//                        color = Color.Black
//                    )
//                } else {
//                    Text(
//                        modifier = Modifier
//                            .height(dp30)
//                            .weight(1f)
//                            .background(color = Color.Transparent, shape = RoundedCornerShape(dp10))
//                            .bounceClick {
//                                releaseDate = today
//                                    .withDayOfMonth(day)
//                                    .toString()
//                            },
//                        text = "$day",
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            for (i in 0 until  (5 * 7) - today.lengthOfMonth()) {
//                Spacer(modifier = Modifier
//                    .height(dp30)
//                    .weight(1f)
//                    .background(color = Color.Transparent))
//            }
//        }
//
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            favoriteMovies.filter { !it.releaseDate.isNullOrEmpty() && releaseDate.isNotEmpty() && it.releaseDate == releaseDate }.forEach { movie ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                        .border(width = dp1, color = Color.White, shape = RoundedCornerShape(dp20))
//                        .bounceClick { onMovieClick(movie.id ?: -1) }
//                ) {
//                    Text(
//                        modifier = Modifier.padding(horizontal = dp10),
//                        text = movie.title ?: ""
//                    )
//                    Text(
//                        modifier = Modifier.padding(horizontal = dp10),
//                        text = movie.releaseDate ?: ""
//                    )
//                }
//            }
//        }
//    }
//}