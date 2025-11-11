package com.valpha.vulcan.model

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.container

open class VulcanConfig(project: Project) {
    val flavorDimensions: NamedDomainObjectContainer<FlavorDimensionConfig> = project.container()
    val variants: NamedDomainObjectContainer<VariantConfig> = project.container()

//    val exclude: SetProperty<String> = project.objects.setProperty()
//    val app: SetProperty<String> = project.objects.setProperty()
//
//    val allFlavors get() = flavorDimension.flatMap { it.allFlavors }
}