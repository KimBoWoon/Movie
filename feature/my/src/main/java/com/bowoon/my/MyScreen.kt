package com.bowoon.my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bowoon.common.Log
import com.bowoon.model.MyData
import com.bowoon.model.PosterSize
import com.bowoon.model.tmdb.TMDBLanguageItem
import com.bowoon.model.tmdb.TMDBRegionResult
import com.bowoon.ui.Title
import com.bowoon.ui.dp16

@Composable
fun MyScreen(
    viewModel: MyVM = hiltViewModel()
) {
    val myState by viewModel.myData.collectAsStateWithLifecycle()

    MyScreen(
        state = myState,
        updateIsAdult = viewModel::updateIsAdult,
        updateLanguage = viewModel::updateLanguage,
        updateRegion = viewModel::updateRegion,
        updateImageQuality = viewModel::updateImageQuality
    )
}

@Composable
fun MyScreen(
    state: MyDataState,
    updateIsAdult: (Boolean) -> Unit,
    updateLanguage: (TMDBLanguageItem) -> Unit,
    updateRegion: (TMDBRegionResult) -> Unit,
    updateImageQuality: (PosterSize) -> Unit
) {
    val isLoading = state is MyDataState.Loading
    var myData by remember { mutableStateOf<MyData?>(null) }
    var checked by remember { mutableStateOf(myData?.isAdult ?: true) }

    when (state) {
        is MyDataState.Loading -> Log.d("myData loading...")
        is MyDataState.Success -> {
            Log.d("${state.myData}")
            myData = state.myData
        }
        is MyDataState.Error -> Log.e(state.throwable.message ?: "something wrong...")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = dp16)
        ) {
            Title(title = "마이페이지")
            Text(text = "메인 업데이트 날짜 ${myData?.mainUpdateLatestDate}")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "성인")
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        updateIsAdult(it)
                    }
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "language")
                ExposedDropdownLanguageMenu(
                    list = myData?.language ?: emptyList(),
                    updateLanguage = updateLanguage
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "region")
                ExposedDropdownRegionMenu(
                    list = myData?.region ?: emptyList(),
                    updateRegion = updateRegion
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "image")
                ExposedDropdownPosterSizeMenu(
                    list = myData?.posterSize ?: emptyList(),
                    updateImageQuality = updateImageQuality
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownLanguageMenu(
    list: List<TMDBLanguageItem>,
    updateLanguage: (TMDBLanguageItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem = list.find { it.isSelected }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = "${selectedItem?.iso6391} (${selectedItem?.name ?: selectedItem?.englishName})",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.sortedBy { it.iso6391 }.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.iso6391} (${it.name ?: it.englishName})",
                            maxLines = 1
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
    list: List<TMDBRegionResult>,
    updateRegion: (TMDBRegionResult) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem = list.find { it.isSelected }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = "${selectedItem?.iso31661} (${selectedItem?.nativeName ?: selectedItem?.englishName})",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.sortedBy { it.iso31661 }.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.iso31661} (${it.nativeName ?: it.englishName})",
                            maxLines = 1
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
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = "${selectedItem?.size}",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.sortedBy { it.size }.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${it.size}",
                            maxLines = 1
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