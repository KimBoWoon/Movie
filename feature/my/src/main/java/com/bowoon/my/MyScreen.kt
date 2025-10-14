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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.getVersionName
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.Language
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.movie.feature.my.R
import com.bowoon.ui.utils.dp16
import com.bowoon.ui.utils.dp50
import com.bowoon.ui.utils.dp500
import com.bowoon.ui.utils.dp56

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("MyScreen", "my screen init")

    val internalData by viewModel.myData.collectAsStateWithLifecycle()
    val movieAppData by viewModel.movieAppData.collectAsStateWithLifecycle()

    MyScreen(
        internalData = internalData,
        movieAppData = movieAppData,
        updateUserData = viewModel::updateUserData
    )
}

@Composable
fun MyScreen(
    internalData: InternalData,
    movieAppData: MovieAppData,
    updateUserData: (InternalData, Boolean) -> Unit
) {
    val context = LocalContext.current
    var isShowChooseDialog by remember { mutableStateOf(false) }
    var chooseDialogItem by remember { mutableStateOf(listOf<Any>()) }
    var selectedOption by remember { mutableStateOf<Any?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Menu.entries.forEach { menu ->
                    when (menu) {
                        Menu.MAIN_UPDATE_DATE -> {
                            DisplayMenuComponent(
                                title = stringResource(R.string.main_update_data_setting),
                                content = internalData.updateDate
                            )
                        }
                        Menu.DARK_MODE_SETTING -> {
                            ChooseMenuComponent(
                                title = stringResource(R.string.dark_mode_setting),
                                content = when (internalData.isDarkMode) {
                                    DarkThemeConfig.FOLLOW_SYSTEM -> stringResource(R.string.dark_mode_setting_system_follow)
                                    DarkThemeConfig.LIGHT -> stringResource(R.string.dark_mode_setting_light)
                                    DarkThemeConfig.DARK -> stringResource(R.string.dark_mode_setting_dark)
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
                                title = stringResource(R.string.is_adult_setting),
                                checked = internalData.isAdult,
                                onClick = { updateUserData(internalData.copy(isAdult = it), false) }
                            )
                        }
                        Menu.IS_AUTO_PLAYING_TRAILER -> {
                            SwitchMenuComponent(
                                title = stringResource(R.string.auto_playing_trailer_setting),
                                checked = internalData.autoPlayTrailer,
                                onClick = { updateUserData(internalData.copy(autoPlayTrailer = it), false) }
                            )
                        }
                        Menu.LANGUAGE -> {
                            ChooseMenuComponent(
                                title = stringResource(R.string.language_setting),
                                content = movieAppData.getLanguage(),
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.language.sortedBy { it.iso6391 }
                                    selectedOption = movieAppData.language.find { it.isSelected }
                                }
                            )
                        }
                        Menu.REGION -> {
                            ChooseMenuComponent(
                                title = stringResource(R.string.region_setting),
                                content = movieAppData.getRegion(),
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.region.sortedBy { it.iso31661 }
                                    selectedOption = movieAppData.region.find { it.isSelected }
                                }
                            )
                        }
                        Menu.IMAGE_QUALITY -> {
                            ChooseMenuComponent(
                                title = stringResource(R.string.image_quality_setting),
                                content = internalData.imageQuality,
                                onClick = {
                                    isShowChooseDialog = true
                                    chooseDialogItem = movieAppData.posterSize
                                    selectedOption = movieAppData.posterSize.find { it.isSelected }
                                }
                            )
                        }
                        Menu.VERSION -> {
                            DisplayMenuComponent(
                                title = stringResource(id = R.string.version_info),
                                content = getVersionName(context = context)
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
                    is Language -> updateUserData(internalData.copy(language = chooseItem.iso6391 ?: ""), true)
                    is Region -> updateUserData(internalData.copy(region = chooseItem.iso31661 ?: ""), true)
                    is PosterSize -> updateUserData(internalData.copy(imageQuality = chooseItem.size ?: ""), true)
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
                        is Language -> "${it.iso6391}_${it.englishName}"
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
                            is Language -> "${item.iso6391} (${item.englishName})"
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