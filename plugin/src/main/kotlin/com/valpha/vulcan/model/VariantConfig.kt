package com.valpha.vulcan.model

import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.LibraryProductFlavor
import com.valpha.vulcan.utility.taggedError
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class VariantConfig @Inject constructor(
    private val objectFactory: ObjectFactory
) : Named, NamedModuleMapping {
    // 注入 extension
    internal lateinit var extension: VulcanConfigExtension

    // 用 MapProperty 存 flavorDimension -> flavor 的映射
    @get:Input
    abstract val flavorMenu: MapProperty<String, String>

    fun flavorMenu(action: FlavorMenuScope.() -> Unit) {
        val scope = objectFactory.newInstance(
            FlavorMenuScope::class.java, this, extension.flavorDimensions
        )
        scope.action()
    }

    @get:Input
    @get:Optional
    abstract val flavorConfig: Property<Action<ApplicationProductFlavor>?>

    fun flavorConfig(action: Action<ApplicationProductFlavor>) {
        flavorConfig.set(action)
    }
}


abstract class FlavorMenuScope @Inject constructor(
    private val variant: VariantConfig,
    private val dimensions: NamedDomainObjectContainer<FlavorDimensionConfig>
) {

    fun select(dimensionName: String, flavorName: String) {

        val dimension = dimensions.findByName(dimensionName)
            ?: error(taggedError("FlavorDimension '$dimensionName' not found"))

        val flavor = dimension.flavors.findByName(flavorName)
            ?: error(taggedError("Flavor '$flavorName' not found in dimension '$dimensionName'."))

        variant.flavorMenu.put(dimensionName, flavorName)
    }
}