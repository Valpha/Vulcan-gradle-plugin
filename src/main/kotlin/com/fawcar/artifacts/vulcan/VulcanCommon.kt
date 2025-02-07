package com.fawcar.artifacts.vulcan

import org.gradle.api.Project
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.getByType

internal val Project.vulcanDimensionFlavors: List<DimensionFlavor>
    get() = rootProject.extensions.getByType<VulcanRootProjectExtension>().dimensionFlavors.get()

internal fun Project.getVulcanFlavors(dimension: String): Set<FlavorType>? {
    return vulcanDimensionFlavors.find { it.dimensionName == dimension }?.flavors
}

internal val Project.vulcanPlatformApplication
    get() = rootProject.extensions.getByType<VulcanRootProjectExtension>().platformApplication.get()

internal val Project.vulcanDimensions: Set<String>
    get() = vulcanDimensionFlavors.map { it.dimensionName }.toSet()

internal val Project.vulcanFlavorsModulePath: Set<String>
    get() = vulcanDimensionFlavors.flatMap { it.flavors.map { it.modulePath } }.toSet()

internal val Project.vulcanFlavorsNames: Set<String>
    get() = vulcanDimensionFlavors.flatMap { it.flavors.map { it.name } }.toSet()



interface VulcanExtension {
    val ids: SetProperty<String>
}