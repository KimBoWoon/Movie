package com.bowoon.data.util

import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.StateFlow

interface DataManager {
    val movieAppData: StateFlow<MovieAppData>
}