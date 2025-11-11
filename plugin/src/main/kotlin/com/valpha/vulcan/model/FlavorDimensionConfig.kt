package com.valpha.vulcan.model

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

abstract class FlavorDimensionConfig: Named  {

    @get:Nested
    abstract val flavors : NamedDomainObjectContainer<Flavor>

}