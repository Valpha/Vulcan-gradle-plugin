package com.valpha.vulcan

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BasePlugin
import com.valpha.vulcan.model.VariantConfig
import com.valpha.vulcan.model.VulcanConfigExtension
import com.valpha.vulcan.utility.VULCAN_VARIANT_DIMENSION
import com.valpha.vulcan.utility.VulcanAttr
import com.valpha.vulcan.utility.log
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.forEach

internal fun Project.configureSubModuleProjectVulcan() {
    this.displayName.log("setup")

    // add module name to root vulcan config
    getRootVulcanConfig().modules += this



    afterEvaluate {
        val vulcanConfig = getRootVulcanConfig()

        plugins.withType<BasePlugin> {
            val androidComponent = extensions.getByType(AndroidComponentsExtension::class)

            if (vulcanConfig.flavorDimensions.any { it.targetModule.get() == this@configureSubModuleProjectVulcan }) {
                // Flavor dimension module, used to define flavor dimension and provide variants.
                disableUnusedVariants(androidComponent, vulcanConfig)
                setAttrProducerConfigurations(androidComponent, vulcanConfig)
            } else if (vulcanConfig.flavorDimensions.flatMap { it.flavors.map { it.targetModule.get() } }
                    .any { it == this@configureSubModuleProjectVulcan }) {
                // Flavor module, used to implement flavor dimensions. No need to config variant dimension.
            } else {
                // App / feature  etc. consumer modules
                configVariantDimensionConfigurations(androidComponent, vulcanConfig)
            }
        }
    }
}

private fun Project.configVariantDimensionConfigurations(
    androidComponent: AndroidComponentsExtension<*, *, *>,
    vulcanConfig: VulcanConfigExtension
) {
    androidComponent.finalizeDsl { extension ->
        "config $VULCAN_VARIANT_DIMENSION in module[${project.name}]".log("configVariants")
        extension as CommonExtension<*, *, *, *, *, *>
        with(extension) {
            flavorDimensions += VULCAN_VARIANT_DIMENSION
            productFlavors {
                vulcanConfig.variants.forEach { variant ->
                    create(variant.name) {
                        this.dimension = VULCAN_VARIANT_DIMENSION
                    }
                }
            }
        }
    }

}


private fun Project.setAttrConsumerConfigurations(
    androidComponent: AndroidComponentsExtension<*, *, *>,
    vulcanConfig: VulcanConfigExtension
) {
    androidComponent.onVariants { variant ->
        // ======================
        // 1. Consumer 属性（作用于 app、feature 等）
        // ======================
        val chosenVariant = variant.productFlavors
            .find { it.first == VULCAN_VARIANT_DIMENSION }?.second
            ?.takeIf { it in vulcanConfig.variants.map { it.name } }
            ?: run {
                return@onVariants
            }

        // --- 给 compile / runtime classpath 添加 consumer 属性
        listOf(
            variant.compileConfiguration,
            variant.runtimeConfiguration
        ).forEach { cfg ->
            cfg.attributes.attribute(VulcanAttr, chosenVariant)
            "Added CONSUMER attr to ${cfg.name}".log("setAttrConsumerConfigurations")
        }
    }

}

private fun Project.setAttrProducerConfigurations(
    androidComponent: AndroidComponentsExtension<*, *, *>,
    vulcanConfig: VulcanConfigExtension
) {
    val tag = "configProducerModule"
    androidComponent.onVariants { variant ->
        val selectedVariant = vulcanConfig.variants.find { variantConfig ->
            val variantMenu = variantConfig.flavorMenu.get()
            variant.productFlavors.all { variantMenu.contains(it.first) && variantMenu[it.first] == it.second }
        } ?: run {
            return@onVariants
        }

        "Module [${name}] Variant [${variant.name}] configuration matched VariantConfig[${selectedVariant.name}].".log(
            tag
        )

        vulcanConfig.flavorDimensions.forEach { flavorDimensionConfig ->
            if (flavorDimensionConfig.targetModule.get() == this) {

                // 1. 找到 producer configurations
                val producerConfigurations = listOf(
                    "${variant.name}ApiElements",
                    "${variant.name}RuntimeElements"
                ).mapNotNull { name ->
                    configurations.findByName(name)
                }

                // 2. 给这些 configuration 附加你希望生产的 attribute
                producerConfigurations.forEach { config ->
                    config.attributes.attribute(VulcanAttr, selectedVariant.name)
                }
            }
        }
    }

}

private fun Project.disableUnusedVariants(
    androidComponent: AndroidComponentsExtension<*, *, *>,
    vulcanConfig: VulcanConfigExtension
) {
    val tag = "disableUnusedVariants"
    androidComponent.beforeVariants { variant ->
        if (vulcanConfig.variants.any { variantConfig ->
                val variantMenu = variantConfig.flavorMenu.get()
                variant.productFlavors.all { variantMenu.contains(it.first) && variantMenu[it.first] == it.second }
            }.not()) {
            variant.enable = false
            "Disabled variant [${variant.name}] in module[$name]".log(tag)
            return@beforeVariants
        }
    }

}

private fun Project.getRootVulcanConfig() = rootProject.extensions.getByType<VulcanConfigExtension>()

