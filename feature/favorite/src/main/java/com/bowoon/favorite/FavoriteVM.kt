package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteVM @Inject constructor(

) : ViewModel() {
    companion object {
        private const val TAG = "FavoriteVM"
    }
}