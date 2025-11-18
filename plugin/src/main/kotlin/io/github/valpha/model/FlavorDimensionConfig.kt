package io.github.valpha.model

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Nested

abstract class FlavorDimensionConfig : Named, NamedModuleMapping {

    @get:Nested
    abstract val flavors: NamedDomainObjectContainer<Flavor>

}