package com.cheeke.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.kotlin.dsl.invoke

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

@Suppress("EnumEntryName")
enum class AppFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(dimension = FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(dimension = FlavorDimension.contentType)
}

fun configureFlavors(
    commonExtension: CommonExtension,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.contentType.name
        productFlavors {
            AppFlavor.entries.forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}