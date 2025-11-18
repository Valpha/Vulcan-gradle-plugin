package io.github.valpha.utility

import org.gradle.api.attributes.Attribute

internal const val VULCAN_VARIANT_DIMENSION = "vulcan_variant_dimension"
internal const val AGP_FLAVOR_DIMENSION_ATTRIBUTE_PREFIX = "com.android.build.api.attributes.ProductFlavor"
// 定义一个自定义 Attribute（你也可以从 root extension 中来确定需要的名字和值）
internal val VulcanAttr =
    Attribute.of("$AGP_FLAVOR_DIMENSION_ATTRIBUTE_PREFIX:${VULCAN_VARIANT_DIMENSION}", String::class.java)