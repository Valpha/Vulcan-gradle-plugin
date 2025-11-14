package com.valpha.vulcan.utility

internal const val KIT_NAME = "Vulcan-Plugin"

internal fun <T> T.warn(tag: String? = null) =
    println("[$KIT_NAME](WARNING):  ${tag?.let { "($it) " } ?: ""}$this")

internal fun Boolean.require(message: String, tag: String? = null) {
    require(this) { "[$KIT_NAME](REQUIRE):  ${tag?.let { "($it) " } ?: ""}$message" }
}

internal inline fun taggedLog(tag: String?, crossinline lazyMessage: () -> String): () -> String {
    return { "[$KIT_NAME]: ${tag?.let { "($it) " } ?: ""}${lazyMessage()}" }
}

internal fun taggedError(message: String): String =
    taggedLog("Error") { message }()

internal fun taggedRequire(lazyMessage: String): () -> String =
    taggedLog("Require") { lazyMessage }


internal fun <T> T.log(tag: String?) =
    println(taggedLog(tag, ::toString)())


fun main() {
    "This is a log message".log("TestTag")
//    require("This is a require message", "TestTag")
}