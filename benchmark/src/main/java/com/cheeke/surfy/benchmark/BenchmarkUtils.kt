package com.cheeke.surfy.benchmark

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.cheeke.benchmark.BuildConfig

val PACKAGE_NAME = buildString {
    append("com.cheeke.movie")
    append(BuildConfig.APP_FLAVOR_SUFFIX)
}

fun UiDevice.waitAndFindObject(selector: BySelector, timeout: Long): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout)) {
        throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
    }

    return findObject(selector)
}

fun UiDevice.findFirstObject(selector: BySelector): UiObject2 = findObject(selector).children.first()