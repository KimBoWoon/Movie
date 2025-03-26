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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.data.repository.LocalMovieAppDataComposition
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.LanguageItem
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.ui.components.Title
import com.bowoon.ui.dp16
import com.bowoon.ui.dp50
import com.bowoon.ui.dp500
import com.bowoon.ui.dp56

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("MyScreen", "my screen init")

    val movieAppData by viewModel.myData.collectAsStateWithLifecycle()

    MyScreen(
        internalData = movieAppData,
        updateUserData = viewModel::updateUserData
    )
}

@Composable
fun MyScreen(
    internalData: InternalData,
    updateUserData: (InternalData, Boolean) -> Unit
) {
    val movieAppData = LocalMovieAppDataComposition.current
    var isShowChooseDialog by remember { mutableStateOf(false) }
    var chooseDialogItem by remember { mutableStateOf(listOf<Any>()) }
    var selectedOption by remember { mutableStateOf<Any?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title(title = "마이페이지")
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Menu.entries.forEach { menu ->
                    when (menu) {
                        Menu.MAIN_UPDATE_DATE -> {
                            DisplayMenuComponent(
                                title = menu.label,
                                content = internalData.updateDate
                            )
                        }
                        Menu.DARK_MODE_SETTING -> {
                            ChooseMenuComponent(
                                title = menu.label,
                                content = when (internalData.isDarkMode) {
                                    DarkThemeConfig.FOLLOW_SYSTEM -> "시스템 설정"
                                    DarkThemeConfig.LIGHT -> "라이트"
                                    DarkThemeConfig.DARK -> "다크"
                                },
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = DarkThemeConfig.entries
                                    selectedOption = internalData.isDarkMode
                                }
                            )
                        }
                        Menu.IS_ADULT -> {
                            SwitchMenuComponent(
                                title = menu.label,
                                checked = internalData.isAdult,
                                onClick = { updateUserData(internalData.copy(isAdult = it), false) }
                            )
                        }
                        Menu.IS_AUTO_PLAYING_TRAILER -> {
                            SwitchMenuComponent(
                                title = menu.label,
                                checked = internalData.autoPlayTrailer,
                                onClick = { updateUserData(internalData.copy(autoPlayTrailer = it), false) }
                            )
                        }
                        Menu.LANGUAGE -> {
                            ChooseMenuComponent(
                                title = menu.label,
                                content = movieAppData.getLanguage(),
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.language ?: emptyList()
                                    selectedOption = movieAppData.language?.find { it.isSelected }
                                }
                            )
                        }
                        Menu.REGION -> {
                            ChooseMenuComponent(
                                title = menu.label,
                                content = movieAppData.getRegion(),
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.region ?: emptyList()
                                    selectedOption = movieAppData.region?.find { it.isSelected }
                                }
                            )
                        }
                        Menu.IMAGE_QUALITY -> {
                            ChooseMenuComponent(
                                title = menu.label,
                                content = internalData.imageQuality,
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.posterSize ?: emptyList()
                                    selectedOption = movieAppData.posterSize?.find { it.isSelected }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (isShowChooseDialog) {
        ChooseDialog(
            list = chooseDialogItem,
            selectedOption = selectedOption,
            dismiss = { isShowChooseDialog = false },
            updateUserData = { chooseItem ->
                when (chooseItem) {
                    is DarkThemeConfig -> updateUserData(internalData.copy(isDarkMode = chooseItem), false)
                    is LanguageItem -> updateUserData(internalData.copy(language = chooseItem.iso6391 ?: ""), true)
                    is Region -> updateUserData(internalData.copy(region = chooseItem.iso31661 ?: ""), true)
                    is PosterSize -> updateUserData(internalData.copy(language = chooseItem.size ?: ""), true)
                }
            }
        )
    }
}

@Composable
fun <T> ChooseDialog(
    list: List<T>,
    selectedOption: T,
    dismiss: () -> Unit,
    updateUserData: (T) -> Unit
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
                key = {
                    when (it) {
                        is DarkThemeConfig -> it.label
                        is LanguageItem -> "${it.iso6391}_${it.englishName}"
                        is Region -> "${it.iso31661}_${it.englishName}"
                        is PosterSize -> it.size ?: ""
                        else -> it ?: ""
                    }
                }
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
                        text = when (item) {
                            is DarkThemeConfig -> item.label
                            is LanguageItem -> "${item.iso6391} (${item.englishName})"
                            is Region -> "${item.iso31661} (${item.englishName})"
                            is PosterSize -> item.size ?: ""
                            else -> ""
                        },
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
    content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp50),
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

@Composable
fun SwitchMenuComponent(
    title: String,
    checked: Boolean,
    onClick: ((Boolean) -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp50),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = dp16),
            text = title
        )
        Switch(
            modifier = Modifier.padding(end = dp16),
            checked = checked,
            onCheckedChange = { onClick(it) }
        )
    }
}

@Composable
fun ChooseMenuComponent(
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
        Row(
            modifier = Modifier.padding(end = dp16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = content)
            Icon(
                modifier = Modifier.rotate(90f),
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "moreIcon"
            )
        }
    }
}