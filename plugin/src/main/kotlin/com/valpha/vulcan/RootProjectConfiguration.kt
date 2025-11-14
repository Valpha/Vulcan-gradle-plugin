package com.valpha.vulcan

import org.gradle.kotlin.dsl.create
import com.valpha.vulcan.model.VulcanConfigExtension
import com.valpha.vulcan.utility.log
import com.valpha.vulcan.utility.taggedError
import org.gradle.api.Project

internal fun Project.configureRootProjectVulcan() {
    "RootProject".log("setup")
    val config = extensions.create<VulcanConfigExtension>("vulcan", this)

    afterEvaluate {
        val tag = "vulcanConfig"
        "vulcan.config.flavorDimensions: ${config.flavorDimensions.map { it.name }}".log(tag)
        "vulcan.config.variants: ${config.variants.map { it.name }}".log(tag)

        "vulcan.config.modules: ${config.modules.map { it.name }}".log(tag)
        // resolve flavor module mapping
        resolveFlavorModuleMapping(config)


        /**
         * 所有的子模块
         */
        val allModules = subprojects.map { it.path }.toSet()

        /**
         * 所有的 Flavor 模块，等同于所有的差异实现层模块
         */
//        val allFlavorsModulePath = vulcanConfigs.allFlavors.map { it.modulePath.get() }.toSet()
//
//        /**
//         * 所有的 FlavorDimension 目标模块，基本等同于接口代理层，差异化选择 flavor
//         */
//        val allTargetsModulePath = vulcanConfigs.flavorDimension.flatMap { it.targetModule.get() }.toSet()
//
//        /**
//         * 所有的排除模块，不参与 vulcan 配置
//         */
//        val allExcludedModules = vulcanConfigs.exclude.get()
//
//        /**
//         * app 顶层模块
//         */
//        val appModules = vulcanConfigs.app.get()
//
//        // check flavor Module exists
//        allModules.containsAll(vulcanFlavorsModulePath).require(
//            "Named flavor module ${vulcanFlavorsModulePath - allModules} was not found in project modules!"
//        )
//        allModules.containsAll(allFlavorsModulePath).require(
//            "Named flavor module ${allFlavorsModulePath - allModules} was not found in project modules!"
//        )
//
//        // check target module exists
//        allModules.containsAll(allTargetsModulePath).require(
//            "Dimension defined target module ${allTargetsModulePath - allModules} was not found in project modules!"
//        )
//
//        // check variants exists
//        vulcanPlatformApplication.forEach {
//            val platform = it.name
//            val platformComponents = it.selectedFlavors
//            vulcanFlavorsNames.containsAll(platformComponents).require(
//                "Platform \"$platform\" defined components ${platformComponents - vulcanFlavorsNames} was not found in defined Flavors!"
//            )
//            "Defined platform \"$platform\", with flavors: $platformComponents".log(tag)
//        }
//        vulcanConfigs.variants.forEach {
//            val allSelectedFlavors = it.selectedFlavorNames.get()
//            vulcanConfigs.allFlavors.map { it.name }.toSet().containsAll(allSelectedFlavors).require(
//                "Variant \"${it.name}\" defined components ${allSelectedFlavors - vulcanFlavorsNames} was not found in defined Flavors!"
//            )
//            it.selectedFlavors.set(vulcanConfigs.allFlavors.filter { it.name in allSelectedFlavors }.toSet())
//        }
//
//        /*tasks.create<GenerateFlavorDimensionsTask>("generateFlavorDimensions") {
//            group = "build"
//            description = "[Vulcan]: 将根项目 build.gradle.kts 中定义的Flavor Dimensions 输出到 xml"
//            doLast {
//                println("Generate flavor dimensions: ${dimensions.get()}")
//            }
//            dimensionXmlFile.set(rootProject.file("vulcan/dimensions.xml"))
//        }*/
//
//        subprojects {
//            val tag = "ConfigSubProjects"
//            afterEvaluate {
//                "config Sub-Projects by rootProject".log(tag)
//            }
//            when (this.path) {
//                in allExcludedModules -> {
//                    "Exclude module ${this.path}".log(tag)
//                }
//
//                in appModules -> {
//                    "App module ${this.path}".log(tag)
////                    extensions.getByType(AndroidComponentsExtension::class.java).let {
////                        configureAndroidEntryModule(it)
////                    }
//                }
//
//                in allFlavorsModulePath -> {
//                    "Flavor module ${this.path}".log(tag)
//                }
//
//                in allTargetsModulePath -> {
//                    "Target module ${this.path}".log(tag)
//                }
//
//                else -> {
//                    "Default business module ${this.path}".log(tag)
//                }
//            }
//        }
    }

}

fun Project.resolveFlavorModuleMapping(config: VulcanConfigExtension): Boolean {
    val tag = "resolveMapping"


    config.flavorDimensions.forEach { dimension ->
        val dimensionTag = tag + ":" + dimension.name
        "check flavor dimension: ${dimension.name}".log(dimensionTag)

        dimension.resolvedProject = dimension.modulePath.orNull?.let {
            config.findProjectByPath(it)
        } ?: config.findProjectByName(dimension.name)
        "FlavorDimension-Module matched, [${dimension.name}<->${dimension.resolvedProject.displayName}]".log(
            dimensionTag
        )
        "xxxxxxx project: ${dimension.targetModule.orNull}".log(dimensionTag)


        dimension.flavors.forEach { flavor ->
            flavor.resolvedProject = flavor.modulePath.orNull?.let {
                config.findProjectByPath(it)
            } ?: config.findProjectByName(flavor.name)
            "Flavor-Module matched, [${flavor.name}<->${flavor.resolvedProject.displayName}]".log(dimensionTag)

            "xxxxxxx project: ${flavor.targetModule.orNull?.parent}".log(dimensionTag)

        }


    }

    return true
}


fun VulcanConfigExtension.findProjectByName(name: String): Project {
    return modules.find { it.name == name } ?: error(taggedError("moduleName(\"$name\") does not exist."))
}

fun VulcanConfigExtension.findProjectByPath(path: String): Project {
    return modules.find { it.path == path } ?: error(taggedError("modulePath(\"$path\") does not exist."))
}
