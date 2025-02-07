package com.fawcar.artifacts.vulcan

internal const val KIT_NAME = "Vulcan"

internal fun <T> T.log(tag: String? = null) =
    println("[$KIT_NAME]: ${tag?.let { "($it) " } ?: ""}$this")

internal fun <T> T.warn(tag: String? = null) =
    println("[$KIT_NAME](WARNING):  ${tag?.let { "($it) " } ?: ""}$this")

internal fun Boolean.require(message: String, tag: String? = null) {
    require(this) { "[$KIT_NAME](REQUIRE):  ${tag?.let { "($it) " } ?: ""}$message" }
}
