package com.bowoon.data.repository

import com.bowoon.model.MovieAppData
import kotlinx.coroutines.flow.StateFlow

interface MovieAppDataRepository {
    val movieAppData: StateFlow<MovieAppData>
}