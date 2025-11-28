package com.bowoon.my

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.common.getVersionName
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.InternalData
import com.bowoon.model.MovieAppData
import com.bowoon.model.PosterSize
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
    val movieAppData by viewModel.movieAppData.movieAppData.collectAsStateWithLifecycle()

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
    var isShowChooseDialog by remember { mutableStateOf(value = false) }
    var isShowLanguageChangeDialog by remember { mutableStateOf(value = false) }
    var chooseDialogItem by remember { mutableStateOf(value = listOf<Any>()) }
    var selectedOption by remember { mutableStateOf<Any?>(value = null) }
    var updateUserDataLambda by remember { mutableStateOf<(Any?) -> Unit>(value = {}) }
    val menuList = mutableListOf<MyMenu>(
        MyMenu.Display(
            label = stringResource(id = R.string.main_update_data_setting),
            content = internalData.updateDate
        ),
        MyMenu.Dialog(
            label = stringResource(id = R.string.dark_mode_setting),
            selected = internalData.isDarkMode,
            list = DarkThemeConfig.entries,
            content = when (internalData.isDarkMode) {
                DarkThemeConfig.FOLLOW_SYSTEM -> stringResource(id = R.string.dark_mode_setting_system_follow)
                DarkThemeConfig.LIGHT -> stringResource(id = R.string.dark_mode_setting_light)
                DarkThemeConfig.DARK -> stringResource(id = R.string.dark_mode_setting_dark)
            },
            updateLambda = { updateUserData(internalData.copy(isDarkMode = it as DarkThemeConfig), false) }
        ),
        MyMenu.Switch(
            label = stringResource(id = R.string.is_adult_setting),
            selected = internalData.isAdult,
            onClick = { updateUserData(internalData.copy(isAdult = it), false) }
        ),
        MyMenu.Switch(
            label = stringResource(id = R.string.auto_playing_trailer_setting),
            selected = internalData.autoPlayTrailer,
            onClick = { updateUserData(internalData.copy(autoPlayTrailer = it), false) }
        ),
        MyMenu.Dialog(
            label = stringResource(id = R.string.language_setting),
            selected = movieAppData.language.find { it.isSelected } ?: "",
            list = movieAppData.language,
            content = "${internalData.language}-${internalData.region}",
            updateLambda = { updateUserData(internalData.copy(language = it as String), true) }
        ),
        MyMenu.Dialog(
            label = stringResource(id = R.string.image_quality_setting),
            selected = internalData.imageQuality,
            list = movieAppData.posterSize,
            content = internalData.imageQuality,
            updateLambda = { updateUserData(internalData.copy(imageQuality = (it as PosterSize).size ?: ""), false) }
        ),
        MyMenu.Display(
            label = stringResource(id = R.string.version_info),
            content = getVersionName(context = context)
        )
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                menuList.forEach { menu ->
                    when (menu) {
                        is MyMenu.Dialog -> {
                            val languageLabel = stringResource(id = R.string.language_setting)

                            DisplayMenuComponent(
                                title = menu.label,
                                content = menu.content,
                                onClick = {
                                    when (menu.label) {
                                        languageLabel -> isShowLanguageChangeDialog = true
                                        else -> isShowChooseDialog = true
                                    }
                                    selectedOption = menu.selected
                                    chooseDialogItem = menu.list
                                    updateUserDataLambda = menu.updateLambda
                                }
                            )
                        }
                        is MyMenu.Display -> {
                            DisplayMenuComponent(
                                title = menu.label,
                                content = menu.content
                            )
                        }
                        is MyMenu.Switch -> {
                            SwitchMenuComponent(
                                title = menu.label,
                                checked = menu.selected,
                                onClick = menu.onClick
                            )
                        }
                    }
                }
            }
        }
    }

    when {
        isShowLanguageChangeDialog -> {
            LanguageChooseMenuComponent(
                internalData = internalData,
                movieAppData = movieAppData,
                updateUserData = updateUserData,
                onDismiss = { isShowLanguageChangeDialog = false }
            )
        }
        isShowChooseDialog -> {
            ChooseDialog(
                list = chooseDialogItem,
                selectedOption = selectedOption,
                dismiss = { isShowChooseDialog = false },
                updateUserData = updateUserDataLambda
            )
        }
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
                .fillMaxWidth(fraction = 0.8f)
                .heightIn(max = dp500)
                .background(color = Color.White)
        ) {
            items(
                items = list,
                key = {
                    when (it) {
                        is DarkThemeConfig -> it.label
                        is PosterSize -> it.size ?: ""
                        else -> it ?: ""
                    }
                }
            ) { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(height = dp56)
                        .selectable(
                            selected = when (item) {
                                is String -> item == selectedOption
                                is PosterSize -> item.isSelected
                                else -> false
                            },
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
                        selected = when (item) {
                            is String -> item == selectedOption
                            is PosterSize -> item.isSelected
                            else -> false
                        },
                        onClick = null
                    )
                    Text(
                        modifier = Modifier.padding(start = dp16),
                        text = when (item) {
                            is DarkThemeConfig -> item.label
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
    content: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dp50)
            .clickable(
                interactionSource = if (onClick == null) null else remember { MutableInteractionSource() },
                indication = if (onClick == null) null else LocalIndication.current
            ) { onClick?.let { it() } },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(start = dp16),
            text = title
        )
        Row(
            modifier = Modifier.padding(end = dp16),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = content)
            if (onClick != null) {
                Icon(
                    modifier = Modifier.rotate(degrees = 90f),
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "moreIcon"
                )
            }
        }
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
            .height(height = dp50),
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
fun LanguageChooseMenuComponent(
    internalData: InternalData,
    movieAppData: MovieAppData,
    updateUserData: (InternalData, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var language by remember { mutableStateOf(value = internalData.language) }
    var region by remember { mutableStateOf(value = internalData.region) }
    val isChooseLanguageExpand = remember { mutableStateOf(value = false) }
    val isChooseRegionExpand = remember { mutableStateOf(value = false) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8f)
                .background(color = MaterialTheme.colorScheme.inverseOnSurface),
            verticalArrangement = Arrangement.Center
        ) {
            DropdownItemComponent(
                list = movieAppData.language.sortedBy { it.iso6391 }.map { "${it.iso6391} (${it.englishName})" },
                selectedOption = language,
                updateUserData = { selectedLanguage ->
                    movieAppData.language.find { language -> "${language.iso6391} (${language.englishName})" == selectedLanguage }?.let {
                        language = it.iso6391 ?: ""
                    }
                },
                isExpand = isChooseLanguageExpand
            )
            HorizontalDivider()
            DropdownItemComponent(
                list = movieAppData.region.sortedBy { it.iso31661 }.map { "${it.iso31661} (${it.englishName})" },
                selectedOption = region,
                updateUserData = { selectedRegion ->
                    movieAppData.region.find { language -> "${language.iso31661} (${language.englishName})" == selectedRegion }?.let {
                        region = it.iso31661 ?: ""
                    }
                },
                isExpand = isChooseRegionExpand
            )
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = dp50)
            ) {
                Box(
                    modifier = Modifier
                        .weight(weight = 1.0f)
                        .fillMaxHeight()
                        .clickable {
                            updateUserData(
                                internalData.copy(language = language, region = region),
                                true
                            )
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
                VerticalDivider(modifier = Modifier.height(height = dp50))
                Box(
                    modifier = Modifier
                        .weight(weight = 1.0f)
                        .fillMaxHeight()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun DropdownItemComponent(
    list: List<String>,
    selectedOption: String,
    updateUserData: (String) -> Unit,
    isExpand: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dp50)
            .clickable { isExpand.value = true },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.testTag(tag = "language"),
            text = selectedOption,
            color = MaterialTheme.colorScheme.onSurface
        )
        DropdownMenu(
            modifier = Modifier.wrapContentSize(),
            expanded = isExpand.value,
            onDismissRequest = { isExpand.value = false }
        ) {
            list.forEach {
                DropdownMenuItem(
                    modifier = Modifier.testTag(tag = it),
                    onClick = {
                        Log.d(it)
                        updateUserData(it)
                        isExpand.value = false
                    },
                    text = { Text(text = it) }
                )
                HorizontalDivider()
            }
        }
    }
}