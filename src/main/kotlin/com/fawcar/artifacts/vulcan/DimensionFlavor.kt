package com.fawcar.artifacts.vulcan

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

data class DimensionFlavor(
    val dimensionName: String,
    val isSupportMulti: Boolean,
    val isMustChoose: Boolean,
    val flavors: Set<FlavorType>
) {
    override fun toString(): String {
        return "{$dimensionName: $flavors}"
    }
}

fun MutableList<DimensionFlavor>.flavorDimension(
    name: String,
    isSupportMulti: Boolean = false,
    isMustChoose: Boolean = true,
    block: MutableSet<FlavorType>.() -> Unit
) = add(DimensionFlavor(name, isSupportMulti, isMustChoose, buildSet(block)))

data class FlavorType(
    val name: String,
    val modulePath: String,
    val ext: ((CommonExtension<*, *, *, *, *, *>).() -> Unit)? = null,
    val extension: (ProductFlavor.() -> Unit)? = null
) {
    override fun toString(): String {
        return "$name(\"$modulePath\")"
    }
}

fun MutableSet<FlavorType>.flavor(
    name: String,
    module: String,
    ext: ((CommonExtension<*, *, *, *, *, *>).() -> Unit)? = null,
    extension: (ProductFlavor.() -> Unit)? = null

) = add(FlavorType(name, module, ext, extension))