package com.bowoon.model

enum class DarkThemeConfig(val label: String) {
    FOLLOW_SYSTEM("시스템 설정"),
    LIGHT("라이트"),
    DARK("다크");

    companion object {
        fun find(value: String): DarkThemeConfig = when (value) {
            FOLLOW_SYSTEM.label -> FOLLOW_SYSTEM
            LIGHT.label -> LIGHT
            DARK.label -> DARK
            else -> throw RuntimeException("정해지지 않은 설정입니다.")
        }
    }
}
