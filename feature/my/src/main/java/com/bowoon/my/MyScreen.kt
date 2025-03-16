package com.bowoon.my

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.bowoon.common.Log
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.asInternalData
import com.bowoon.ui.Title
import com.bowoon.ui.dp16
import com.bowoon.ui.dp50
import com.bowoon.ui.dp500
import com.bowoon.ui.dp56

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("MyScreen", "my screen init")

    val movieAppData = LocalMovieAppDataComposition.current

    MyScreen(
        movieAppData = movieAppData,
        updateUserData = viewModel::updateUserData
    )
}

@Composable
fun MyScreen(
    movieAppData: MovieAppData,
    updateUserData: (InternalData, Boolean) -> Unit
) {
    var isShowChooseDialog by remember { mutableStateOf(false) }
    var chooseDialogItem by remember { mutableStateOf(emptyList<String>()) }
    var selectedOption by remember { mutableStateOf("") }
    var updateData by remember { mutableStateOf<(String) -> Unit>({ Log.d(it) }) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title(title = "마이페이지")
        DisplayMenuComponent(
            title = "메인 업데이트 날짜",
            content = movieAppData.updateDate,
            onClick = null
        )
        DisplayMenuComponent(
            title = "다크모드 설정",
            content = movieAppData.isDarkMode.label,
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = DarkThemeConfig.entries.map { it.label }
                selectedOption = movieAppData.isDarkMode.label
                updateData = {
                    updateUserData(movieAppData.asInternalData().copy(isDarkMode = DarkThemeConfig.find(it)), false)
                }
            }
        )
        DisplayMenuComponent(
            title = "성인",
            content = when (movieAppData.isAdult) {
                true -> "성인"
                false -> "미성년자"
            },
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = listOf("성인", "미성년자")
                selectedOption = when (movieAppData.isAdult) {
                    true -> "성인"
                    false -> "미성년자"
                }
                updateData = {
                    when (it) {
                        "성인" -> updateUserData(movieAppData.asInternalData().copy(isAdult = true), false)
                        "미성년자" -> updateUserData(movieAppData.asInternalData().copy(isAdult = false), false)
                    }
                }
            }
        )
        DisplayMenuComponent(
            title = "예고편 자동 재생",
            content = when (movieAppData.autoPlayTrailer) {
                true -> "재생"
                false -> "정지"
            },
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = listOf("재생", "정지")
                selectedOption = when (movieAppData.autoPlayTrailer) {
                    true -> "재생"
                    false -> "정지"
                }
                updateData = {
                    when (it) {
                        "재생" -> updateUserData(movieAppData.asInternalData().copy(autoPlayTrailer = true), false)
                        "정지" -> updateUserData(movieAppData.asInternalData().copy(autoPlayTrailer = false), false)
                    }
                }
            }
        )
        DisplayMenuComponent(
            title = "언어",
            content = movieAppData.getLanguage(),
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = movieAppData.language?.sortedBy { it.iso6391 }?.map { "${it.iso6391} (${it.englishName})" } ?: emptyList()
                selectedOption = movieAppData.getLanguage()
                updateData = { selectedOption ->
                    movieAppData.language?.find { "${it.iso6391} (${it.englishName})" == selectedOption }?.also {
                        updateUserData(movieAppData.asInternalData().copy(language = it.iso6391 ?: ""), true)
                    }
                }
            }
        )
        DisplayMenuComponent(
            title = "지역",
            content = movieAppData.getRegion(),
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = movieAppData.region?.sortedBy { it.iso31661 }?.map { "${it.iso31661} (${it.englishName})" } ?: emptyList()
                selectedOption = movieAppData.getRegion()
                updateData = { selectedOption ->
                    movieAppData.region?.find { "${it.iso31661} (${it.englishName})" == selectedOption }?.also {
                        updateUserData(movieAppData.asInternalData().copy(region = it.iso31661 ?: ""), true)
                    }
                }
            }
        )
        DisplayMenuComponent(
            title = "이미지 퀄리티",
            content = movieAppData.imageQuality,
            onClick = {
                isShowChooseDialog = true
                chooseDialogItem = movieAppData.posterSize?.mapNotNull { it.size } ?: emptyList()
                selectedOption = movieAppData.imageQuality
                updateData = {
                    updateUserData(movieAppData.asInternalData().copy(imageQuality = it), true)
                }
            }
        )
    }

    if (isShowChooseDialog) {
        ChooseDialog(
            list = chooseDialogItem,
            selectedOption = selectedOption,
            dismiss = { isShowChooseDialog = false },
            updateUserData = {
                updateData(it)
            }
        )
    }
}

@Composable
fun ChooseDialog(
    list: List<String>,
    selectedOption: String,
    dismiss: () -> Unit,
    updateUserData: (String) -> Unit
) {
    Dialog(
        onDismissRequest = { dismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(max = dp500)
                .background(color = Color.White)
        ) {
            items(
                items = list,
                key = { it }
            ) { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(dp56)
                        .selectable(
                            selected = (item == selectedOption),
                            onClick = {
                                updateUserData(item)
                                dismiss()
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = dp16),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item == selectedOption),
                        onClick = null
                    )
                    Text(
                        modifier = Modifier.padding(start = dp16),
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayMenuComponent(
    title: String,
    content: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp50)
            .clickable { onClick?.let { it() } },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = dp16),
            text = title
        )
        Text(
            modifier = Modifier.padding(end = dp16),
            text = content
        )
    }
}