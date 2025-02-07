package com.fawcar.artifacts.vulcan

import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.VariantBuilder
import com.android.build.gradle.AppPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.hasPlugin

internal fun Project.configureAndroidEntryModule(
    androidExtension: AndroidComponentsExtension<*, *, *>,
) {
    with(androidExtension) {
        finalizeDsl { extension ->
            configurePlatformProducts(extension)
        }
    }
}

internal val VulcanExtension.isVariantsModule: Boolean
    get() = ids.get().isNotEmpty()

internal fun Project.configureAndroidLibraryModule(
    androidExtension: AndroidComponentsExtension<*, *, *>,
) {
    val vulcanExtension = extensions.create<VulcanExtension>("vulcan")

    with(androidExtension) {
        finalizeDsl { extension ->
            val tag = "finalizeDsl"
            val definedDimensions = vulcanExtension.ids.get()
            if (!vulcanExtension.isVariantsModule) {
                // 依赖方的配置
                configurePlatformProducts(extension)
            } else {
                // 差异化变体模块配置
                configureVariants(extension, definedDimensions)
            }
            "finished".log(tag)
        }
        beforeVariants { variantBuilder ->
            if (vulcanExtension.isVariantsModule) {
                configureFilterUnusedVariants(variantBuilder)
            }
        }
    }
}

fun Project.configureFilterUnusedVariants(variantBuilder: VariantBuilder) {
    val tag = "configureFilterUnusedVariants"
    val variants = variantBuilder.productFlavors.map { it.second }
    val isEnabled = vulcanPlatformApplication.any { it.selectedFlavors.containsAll(variants) }
    if (!isEnabled) {
        "variant ${variantBuilder.name} is disabled".log(tag)
    }
    variantBuilder.enable = isEnabled
}

fun Project.configureVariants(
    extension: CommonExtension<*, *, *, *, *, *>, dimensions: Set<String>
) {
    val tag = "configureVariants"

    with(extension) {
        dimensions.forEach { dimensionName ->
            val flavors = getVulcanFlavors(dimensionName) ?: kotlin.run {
                buildString {
                    append("project ($name) defined flavor dimension: $dimensionName, ")
                    append("but is NOT defined in vulcanFlavors! SKIPPED.")
                }.warn(tag)
                return@forEach
            }

            flavorDimensions += dimensionName
            "add Dimension(\"$dimensionName\")".log(tag)
            flavors.forEach { flavor ->
                productFlavors {
                    create(flavor.name) {
                        dimension = dimensionName
                        flavor.extension?.invoke(this)
                    }
                }
                dependencies {
                    add("${flavor.name}Implementation", project(flavor.modulePath))
                }
                buildString {
                    append("add Flavor(${flavor.name}: \"${flavor.modulePath}\") ")
                    append("belongs to Dimension(\"${dimensionName}\")")
                }.log(tag)
            }
        }
    }
}

fun Project.configurePlatformProducts(extension: CommonExtension<*, *, *, *, *, *>) {
    val tag = "configurePlatformProducts"

    with(extension) {
        flavorDimensions += "platform"
        vulcanPlatformApplication.forEach {
            val selectedDimenFlavors = it.selectedFlavors.map { moduleName: String ->
                vulcanDimensionFlavors.first { it.flavors.any { it.name == moduleName } }.dimensionName to moduleName
            }
            productFlavors {
                create(it.name) {
                    dimension = "platform"
                    selectedDimenFlavors.forEach {
                        missingDimensionStrategy(it.first, it.second)
                        buildConfigField("String", it.first.uppercase(), "\"${it.second}\"")
//                        "add buildConfig field: ${it.first.uppercase()} = \"${it.second}\"".log(tag)
                    }
                    if (plugins.hasPlugin(AppPlugin::class) && this is ApplicationProductFlavor) {
                        it.configuration?.invoke(this)
                        "applicationProductFlavor. AppFlavor.configuration was called.".log(tag)
                    }
                    "add missing strategy for \"${it.name}\": $selectedDimenFlavors".log(tag)
                }
            }
        }
    }

}
