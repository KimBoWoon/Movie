package com.bowoon.data.util

import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.StateFlow

interface ApplicationData {
    val movieAppData: StateFlow<MovieAppData>
}