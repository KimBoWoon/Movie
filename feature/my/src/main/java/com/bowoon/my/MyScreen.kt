package com.bowoon.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.firebase.LocalFirebaseLogHelper
import com.bowoon.model.DarkThemeConfig
import com.bowoon.model.LanguageItem
import com.bowoon.model.TMDBConfiguration
import com.bowoon.model.PosterSize
import com.bowoon.model.Region
import com.bowoon.model.UserData
import com.bowoon.ui.Title
import com.bowoon.ui.dp150
import com.bowoon.ui.dp16
import com.bowoon.ui.dp250

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    LocalFirebaseLogHelper.current.sendLog("MyScreen", "my screen init")

    val myState by viewModel.myData.collectAsStateWithLifecycle()

    MyScreen(
        myDataState = myState,
        updateUserData = viewModel::updateUserData
    )
}

@Composable
fun MyScreen(
    myDataState: MyDataState,
    updateUserData: (UserData, Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (myDataState) {
            is MyDataState.Loading -> {
                Log.d("myData loading...")
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MyDataState.Success -> {
                Log.d("myData -> ${myDataState.myData}")
                Log.d("userData -> ${myDataState.userData}")
                MyDataComponent(
                    myData = myDataState.myData,
                    userData = myDataState.userData,
                    updateUserData = updateUserData
                )
            }
            is MyDataState.Error -> Log.e(myDataState.throwable.message ?: "something wrong...")
        }
    }
}

@Composable
fun MyDataComponent(
    myData: TMDBConfiguration,
    userData: UserData,
    updateUserData: (UserData, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title(title = "마이페이지")
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            text = "메인 업데이트 날짜 ${userData.updateDate}"
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "다크모드 설정")
            Switch(
                checked = when (userData.isDarkMode) {
                    DarkThemeConfig.DARK -> true
                    DarkThemeConfig.LIGHT -> false
                    else -> false
                },
                onCheckedChange = {
                    when (it) {
                        true -> DarkThemeConfig.DARK
                        false -> DarkThemeConfig.LIGHT
                        else -> DarkThemeConfig.FOLLOW_SYSTEM
                    }.run {
                        updateUserData(userData.copy(isDarkMode = this), false)
                    }
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "성인")
            Switch(
                checked = userData.isAdult,
                onCheckedChange = {
                    updateUserData(userData.copy(isAdult = it), false)
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "예고편 자동 재생")
            Switch(
                checked = userData.autoPlayTrailer,
                onCheckedChange = {
                    updateUserData(userData.copy(autoPlayTrailer = it), false)
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "언어")
            ExposedDropdownLanguageMenu(
                list = myData.language ?: emptyList(),
                updateLanguage = {
                    updateUserData(userData.copy(language = it.iso6391 ?: ""), true)
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "지역")
            ExposedDropdownRegionMenu(
                list = myData.region ?: emptyList(),
                updateRegion = {
                    updateUserData(userData.copy(region = it.iso31661 ?: ""), true)
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "이미지 퀄리티")
            ExposedDropdownPosterSizeMenu(
                list = myData.posterSize ?: emptyList(),
                updateImageQuality = {
                    updateUserData(userData.copy(imageQuality = it.size ?: ""), true)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownLanguageMenu(
    list: List<LanguageItem>,
    updateLanguage: (LanguageItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem = list.find { it.isSelected }

    ExposedDropdownMenuBox(
        modifier = Modifier.width(dp150),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).wrapContentSize(),
            readOnly = true,
            value = "${selectedItem?.iso6391} (${selectedItem?.englishName})",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1
        )

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = dp250),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.sortedBy { it.iso6391 }.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.iso6391} (${it.englishName})",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        selectedItem = it
                        expanded = false
                        updateLanguage(it)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownRegionMenu(
    list: List<Region>,
    updateRegion: (Region) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem = list.find { it.isSelected }

    ExposedDropdownMenuBox(
        modifier = Modifier.width(dp150),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).wrapContentSize(),
            readOnly = true,
            value = "${selectedItem?.iso31661} (${selectedItem?.nativeName ?: selectedItem?.englishName})",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1
        )

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = dp250),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.sortedBy { it.iso31661 }.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.iso31661} (${it.nativeName ?: it.englishName})",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        selectedItem = it
                        expanded = false
                        updateRegion(it)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownPosterSizeMenu(
    list: List<PosterSize>,
    updateImageQuality: (PosterSize) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem = list.find { it.isSelected }

    ExposedDropdownMenuBox(
        modifier = Modifier.width(dp150),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = "${selectedItem?.size}",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            maxLines = 1
        )

        ExposedDropdownMenu(
            modifier = Modifier.heightIn(max = dp250),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.size}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        selectedItem = it
                        expanded = false
                        updateImageQuality(it)
                    }
                )
            }
        }
    }
}