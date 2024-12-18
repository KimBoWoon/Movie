package com.bowoon.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(

) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
    }
}