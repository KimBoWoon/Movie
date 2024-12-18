package com.bowoon.my

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyVM @Inject constructor(

) : ViewModel() {
    companion object {
        private const val TAG = "MyVM"
    }
}