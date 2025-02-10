package com.fawcar.artifacts.vulcan

import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.container
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.setProperty

interface VulcanRootProjectExtension {

    val dimensionFlavors: ListProperty<DimensionFlavor>

    val platformApplication: ListProperty<AppFlavor>

    val outputXmlFile: RegularFileProperty
}


abstract class Flavor : Named {
    @get:Input
    abstract val modulePath: Property<String>

    @get:Input
    @get:Optional
    abstract var ext: ((CommonExtension<*, *, *, *, *, *>).() -> Unit)?

    @get:Input
    @get:Optional
    abstract var extension: (ProductFlavor.() -> Unit)?
    override fun toString(): String {
        return "(\"${modulePath.get()}\"${if (ext != null) ", ext" else ""}${if (extension != null) ", extension" else ""})"
    }
}

abstract class FlavorDimension : Named {
    @get:Input
    abstract val targetModule: SetProperty<String>

    @get:Nested
    abstract val flavors: NamedDomainObjectContainer<Flavor>

    var isSupportMulti: Boolean = false
    var isMustChoose: Boolean = true

    val allFlavors: Set<Flavor> get() = flavors.toSet()

    override fun toString(): String {
        return """
        |{
        |    name: ${name},
        |    targetModule: ${targetModule.get()},
        |    flavors: ${flavors.asMap},
        |    isSupportMulti: $isSupportMulti,
        |    isMustChoose: $isMustChoose
        |}
        """.trimMargin()
    }
}

abstract class Variant : Named {
    @get:Input
    abstract val selectedFlavorNames: SetProperty<String>

    @get:Internal
    internal abstract val selectedFlavors: SetProperty<Flavor>

    @get:Input
    @get:Optional
    abstract var configuration: (ApplicationProductFlavor.() -> Unit)?

    override fun toString(): String {
        return """
        |{
        |    name: ${name},
        |    selectedFlavors: ${this.selectedFlavorNames.get()},
        |    configuration: ${configuration?.run { "true" } ?: "null"}
        |}
        """.trimMargin()
    }
}

open class VulcanConfigs(project: Project) {
    val flavorDimension: NamedDomainObjectContainer<FlavorDimension> = project.container(FlavorDimension::class)
    val variants: NamedDomainObjectContainer<Variant> = project.container(Variant::class)

    val exclude: SetProperty<String> = project.objects.setProperty()
    val app: SetProperty<String> = project.objects.setProperty()

    val allFlavors get() = flavorDimension.flatMap { it.allFlavors }
}

internal fun Project.configureRootProjectVulcan() {
    val vulcanExtension = extensions.create<VulcanRootProjectExtension>("vulcan")
//
    val vulcanConfigs = extensions.create<VulcanConfigs>("vulcan1", project)

    afterEvaluate {
        val tag = "afterEvaluate"
        "Dimensions defined. dimensions:$vulcanDimensionFlavors".log(tag)

        "___DEBUG___.vulcanConfigs:flavors ${vulcanConfigs.flavorDimension.toList()}".log(tag)
        "___DEBUG___.vulcanConfigs:variants ${vulcanConfigs.variants.toList()}".log(tag)

        /**
         * 所有的子模块
         */
        val allModules = subprojects.map { it.path }.toSet()

        /**
         * 所有的 Flavor 模块，等同于所有的差异实现层模块
         */
        val allFlavorsModulePath = vulcanConfigs.allFlavors.map { it.modulePath.get() }.toSet()

        /**
         * 所有的 FlavorDimension 目标模块，基本等同于接口代理层，差异化选择 flavor
         */
        val allTargetsModulePath = vulcanConfigs.flavorDimension.flatMap { it.targetModule.get() }.toSet()

        /**
         * 所有的排除模块，不参与 vulcan 配置
         */
        val allExcludedModules = vulcanConfigs.exclude.get()

        /**
         * app 顶层模块
         */
        val appModules = vulcanConfigs.app.get()

        // check flavor Module exists
        allModules.containsAll(vulcanFlavorsModulePath).require(
            "Named flavor module ${vulcanFlavorsModulePath - allModules} was not found in project modules!"
        )
        allModules.containsAll(allFlavorsModulePath).require(
            "Named flavor module ${allFlavorsModulePath - allModules} was not found in project modules!"
        )

        // check target module exists
        allModules.containsAll(allTargetsModulePath).require(
            "Dimension defined target module ${allTargetsModulePath - allModules} was not found in project modules!"
        )

        // check variants exists
        vulcanPlatformApplication.forEach {
            val platform = it.name
            val platformComponents = it.selectedFlavors
            vulcanFlavorsNames.containsAll(platformComponents).require(
                "Platform \"$platform\" defined components ${platformComponents - vulcanFlavorsNames} was not found in defined Flavors!"
            )
            "Defined platform \"$platform\", with flavors: $platformComponents".log(tag)
        }
        vulcanConfigs.variants.forEach {
            val allSelectedFlavors = it.selectedFlavorNames.get()
            vulcanConfigs.allFlavors.map { it.name }.toSet().containsAll(allSelectedFlavors).require(
                "Variant \"${it.name}\" defined components ${allSelectedFlavors - vulcanFlavorsNames} was not found in defined Flavors!"
            )
            it.selectedFlavors.set(vulcanConfigs.allFlavors.filter { it.name in allSelectedFlavors }.toSet())
        }

        /*tasks.create<GenerateFlavorDimensionsTask>("generateFlavorDimensions") {
            group = "build"
            description = "[Vulcan]: 将根项目 build.gradle.kts 中定义的Flavor Dimensions 输出到 xml"
            doLast {
                println("Generate flavor dimensions: ${dimensions.get()}")
            }
            dimensionXmlFile.set(rootProject.file("vulcan/dimensions.xml"))
        }*/

        subprojects {
            val tag = "ConfigSubProjects"
            afterEvaluate {
                "config Sub-Projects by rootProject".log(tag)
            }
            when (this.path) {
                in allExcludedModules -> {
                    "Exclude module ${this.path}".log(tag)
                }

                in appModules -> {
                    "App module ${this.path}".log(tag)
//                    extensions.getByType(AndroidComponentsExtension::class.java).let {
//                        configureAndroidEntryModule(it)
//                    }
                }

                in allFlavorsModulePath -> {
                    "Flavor module ${this.path}".log(tag)
                }

                in allTargetsModulePath -> {
                    "Target module ${this.path}".log(tag)
                }

                else -> {
                    "Default business module ${this.path}".log(tag)
                }
            }
        }
    }

}

/*
abstract class GenerateFlavorDimensionsTask : DefaultTask() {
    @get:InputFile
    abstract val dimensionXmlFile: RegularFileProperty

    @get:Internal
    abstract val dimensions: SetProperty<DimensionType>

    @TaskAction
    fun generateDimensions() {
        val parser = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(dimensionXmlFile.get().asFile)

        parser.documentElement.childNodes.forEach { node ->
            if (node is Element && node.tagName == "dimension") {
                val name = node.getAttribute("name")
                val isSupportMulti = if (node.hasAttribute("isSupportMulti")) {
                    node.getAttribute("isSupportMulti").toBoolean()
                } else {
                    false
                }
                val isMustChoose = if (node.hasAttribute("isMustChoose")) {
                    node.getAttribute("isMustChoose").toBoolean()
                } else {
                    true
                }
                if (name.isNotBlank()) {
//                    dimensions.add(DimensionType(name, isSupportMulti, isMustChoose))
                }
            }
        }
    }
}*/
