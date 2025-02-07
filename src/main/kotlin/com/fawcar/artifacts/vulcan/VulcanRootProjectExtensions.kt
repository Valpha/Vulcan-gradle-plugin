package com.fawcar.artifacts.vulcan

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.create

interface VulcanRootProjectExtension {

    val dimensionFlavors: ListProperty<DimensionFlavor>

    val platformApplication: ListProperty<AppFlavor>

    val outputXmlFile: RegularFileProperty
}

internal fun Project.configureRootProjectVulcan() {
    extensions.create<VulcanRootProjectExtension>("vulcan")

    afterEvaluate {
        val tag = "afterEvaluate"
        "Dimensions defined. dimensions:$vulcanDimensionFlavors".log(tag)

        // check Module exists
        val allModules = subprojects.mapTo(HashSet()) { it.path }
        allModules.containsAll(vulcanFlavorsModulePath).require(
            "Named flavor module ${vulcanFlavorsModulePath - allModules} was not found in project modules!"
        )

        // check platform variants exists
        vulcanPlatformApplication.forEach {
            val platform = it.name
            val platformComponents = it.selectedFlavors
            vulcanFlavorsNames.containsAll(platformComponents).require(
                "Platform \"$platform\" defined components ${platformComponents - vulcanFlavorsNames} was not found in defined Flavors!"
            )
            "Defined platform \"$platform\", with flavors: $platformComponents".log(tag)

        }


        /*tasks.create<GenerateFlavorDimensionsTask>("generateFlavorDimensions") {
            group = "build"
            description = "[Vulcan]: 将根项目 build.gradle.kts 中定义的Flavor Dimensions 输出到 xml"
            doLast {
                println("Generate flavor dimensions: ${dimensions.get()}")
            }
            dimensionXmlFile.set(rootProject.file("vulcan/dimensions.xml"))
        }*/
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
