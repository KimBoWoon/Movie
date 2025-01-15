package com.bowoon.data.repository

interface MainMenuRepository {
    suspend fun syncWith(isForce: Boolean): Boolean
}