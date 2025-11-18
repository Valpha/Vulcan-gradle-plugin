package com.valpha.vulcan

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.valpha.vulcan.model.FlavorDimensionConfig
import com.valpha.vulcan.model.VariantConfig
import org.gradle.kotlin.dsl.create
import com.valpha.vulcan.model.VulcanConfigExtension
import com.valpha.vulcan.model.findProjectByName
import com.valpha.vulcan.utility.VULCAN_VARIANT_DIMENSION
import com.valpha.vulcan.utility.log
import com.valpha.vulcan.utility.logState
import com.valpha.vulcan.utility.require
import com.valpha.vulcan.utility.taggedRequire
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType

internal fun Project.configureRootProjectVulcan() {
    "RootProject".log("setup")
    val config = extensions.create<VulcanConfigExtension>("vulcan")

    afterEvaluate {
        val tag = "vulcanConfig"
        "vulcan.config.flavorDimensions: ${config.flavorDimensions.map { it.name }}".log(tag)
        "vulcan.config.variants: ${config.variants.map { it.name }}".log(tag)
        "vulcan.config.modules: ${config.modules.map { it.name }}".log(tag)

        // resolve flavor module mapping
        resolveFlavorVariantsModuleMapping(config)

        // check variants config correctness
        checkVariantsConfig(config)

        configFlavorAndDimensionModule(config.flavorDimensions)
    }
}

private fun Project.configFlavorAndDimensionModule(flavorDimensions: NamedDomainObjectContainer<FlavorDimensionConfig>) {
    val tag = "configFlavorAndDimensionModule"
    tag.logState()

    flavorDimensions.forEach { flavorDimension ->
        val dimensionTag = tag + ":" + flavorDimension.name
        "Configuring flavor dimension: ${flavorDimension.name}".log(dimensionTag)

        flavorDimension.targetModule.get().afterEvaluate {
            val androidComponent = extensions.findByType(LibraryAndroidComponentsExtension::class)
            requireNotNull(
                androidComponent,
                taggedRequire { "Cannot find AndroidComponentsExtension, please make sure Vulcan is applied to an Android module." })

            androidComponent.finalizeDsl { extension ->
                "config FlavorDimension[${flavorDimension.name}] for module [${name}]".log(dimensionTag)

                with(extension) {
                    this.flavorDimensions += flavorDimension.name
                    productFlavors {
                        flavorDimension.flavors.forEach {
                            create(it.name) {
                                this.dimension = flavorDimension.name
                                it.flavorConfig.orNull?.execute(this)
                            }
                            dependencies {
                                add("${it.name}Implementation", it.targetModule)
                            }

                        }
                    }

                }

            }


        }

    }
}

fun Project.resolveFlavorVariantsModuleMapping(config: VulcanConfigExtension): Boolean {
    val tag = "resolveMapping"
    tag.logState()

    val flavorNamingChecker = mutableSetOf<String>()

    config.flavorDimensions.forEach { dimension ->
        val dimensionTag = tag + ":" + dimension.name
        "check flavor dimension: ${dimension.name}".log(dimensionTag)

        dimension.targetModule.orNull ?: run {
            dimension.targetModule.set(config.findProjectByName(dimension.name))
        }
        "FlavorDimension-Module matched, [${dimension.name}]<->[${dimension.targetModule.get().displayName}]".log(
            dimensionTag
        )

        dimension.flavors.forEach { flavor ->
            flavorNamingChecker.add(flavor.name).require {
                "Flavor name was already existed. AGP require all product flavor name must be unique!"
            }
            flavor.targetModule.orNull ?: run {
                flavor.targetModule.set(config.findProjectByName(flavor.name))
            }
            "Flavor-Module matched, [${flavor.name}]<->[${flavor.targetModule.get().displayName}]".log(dimensionTag)
        }


    }

    config.variants.forEach { variant ->
        val variantTag = tag + ":" + variant.name
        variant.targetModule.orNull ?: run {
            variant.targetModule.set(config.findProjectByName(variant.name))
        }
        "Variant-Module matched, [${variant.name}]<->[${variant.targetModule.get().displayName}]".log(variantTag)
    }
    return true
}

private fun Project.checkVariantsConfig(config: VulcanConfigExtension) {
    val tag = "variantsCheck"
    tag.logState()

    val variants = config.variants
    variants.forEach {
        val variantTag = tag + ":" + it.name
        "Checking variant: ${it.name}".log(variantTag)
        it.flavorMenu.get().forEach { (dimen, flavor) ->
            "menu for dimension: [$dimen] -> [$flavor]".log(variantTag)
        }
    }
}



