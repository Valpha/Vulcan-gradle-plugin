package com.valpha.vulcan

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BaseFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.LibraryPlugin
import com.valpha.vulcan.model.VariantConfig
import com.valpha.vulcan.model.VulcanConfigExtension
import com.valpha.vulcan.utility.log
import com.valpha.vulcan.utility.logState
import com.valpha.vulcan.utility.taggedRequire
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

internal fun Project.configureSubModuleProjectVulcan() {
    this.displayName.log("setup")

    // add module name to root vulcan config
    getRootVulcanConfig().modules += this

    afterEvaluate {
        val vulcanConfig = getRootVulcanConfig()
        vulcanConfig.variants.forEach { variant ->
//            project.plugins.withType(BasePlugin::class) {
//                val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class)
//                androidComponents.finalizeDsl {
//                    it as CommonExtension<*, *, *, *, *, *>
//                    "config MissingDimensionStrategy for Module [${project.name}] Variant [${variant.name}]"
//                        .log("configMissingDimensionStrategy:${variant.name}")
//                    it.configMissingDimensionStrategy(variant)
//                }
//            }

//            project.plugins.withType(LibraryPlugin::class) {
//                val androidComponents = project.extensions.getByType(LibraryAndroidComponentsExtension::class)
//                androidComponents.finalizeDsl {
//                    it as CommonExtension<*, *, *, *, *, *>
//                    "Finalize DSL for LibraryModule [${project.name}]".log(tag)
//                    it.defaultConfig {
//                        variant.flavorMenu.get().forEach { (flavorDimension, flavor) ->
//                            missingDimensionStrategy(flavorDimension, flavor)
//                        }
//                    }
//                }
//            }
        }
    }
}

private fun CommonExtension<*, *, *, *, *, *>.configMissingDimensionStrategy(
    variant: VariantConfig
) {
    defaultConfig {
        variant.flavorMenu.get().forEach { (flavorDimension, flavor) ->
            missingDimensionStrategy(flavorDimension, flavor)
            "select(${flavorDimension}-->$flavor)".log("configMissingDimensionStrategy:${variant.name}")
        }
    }
}

private fun Project.getRootVulcanConfig() = rootProject.extensions.getByType<VulcanConfigExtension>()

