package io.github.valpha.model

import io.github.valpha.utility.taggedError
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class VulcanConfigExtension @Inject constructor(objectFactory: ObjectFactory) {
    internal val modules: MutableSet<Project> = mutableSetOf()
    val flavorDimensions = objectFactory.domainObjectContainer(FlavorDimensionConfig::class)
    val variants = objectFactory.domainObjectContainer(VariantConfig::class) {
        objectFactory.newInstance<VariantConfig>(it)
            .apply { extension = this@VulcanConfigExtension }
    }

}

fun VulcanConfigExtension.findProjectByName(name: String): Project {
    return modules.find { it.name == name } ?: error(taggedError("moduleName(\"$name\") does not exist."))
}