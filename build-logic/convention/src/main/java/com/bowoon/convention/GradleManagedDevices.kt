package com.bowoon.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

/**
 * gradle이 앱을 테스트하는데 사용하는 테스트 기기를 설정
 *
 * https://developer.android.com/studio/test/gradle-managed-devices?hl=ko
 */
internal fun configureGradleManagedDevices(
    commonExtension: CommonExtension,
) {
    val pixel4 = DeviceConfig(device = "Pixel 4", apiLevel = 30, systemImageSource = "aosp-atd")
    val pixel6 = DeviceConfig(device = "Pixel 6", apiLevel = 31, systemImageSource = "aosp")
    val pixelC = DeviceConfig(device = "Pixel C", apiLevel = 30, systemImageSource = "aosp-atd")

    val allDevices = listOf(pixel4, pixel6, pixelC)
    val ciDevices = listOf(pixel4, pixelC)

    commonExtension.testOptions.apply {
        managedDevices {
            allDevices {
                allDevices.forEach { deviceConfig ->
                    maybeCreate(name = deviceConfig.taskName, type = ManagedVirtualDevice::class.java).apply {
                        device = deviceConfig.device
                        apiLevel = deviceConfig.apiLevel
                        systemImageSource = deviceConfig.systemImageSource
                    }
                }
            }
            groups {
                maybeCreate(name = "ci").apply {
                    ciDevices.forEach { deviceConfig ->
                        targetDevices.add(element = localDevices[deviceConfig.taskName])
                    }
                }
            }
        }
    }
}

private data class DeviceConfig(
    val device: String,
    val apiLevel: Int,
    val systemImageSource: String,
) {
    val taskName = buildString {
        append(device.lowercase().replace(oldValue = " ", newValue = ""))
        append("api")
        append(apiLevel.toString())
        append(systemImageSource.replace(oldValue = "-", newValue = ""))
    }
}
