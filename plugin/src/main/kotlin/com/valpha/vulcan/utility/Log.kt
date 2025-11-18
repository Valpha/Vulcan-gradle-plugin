package com.valpha.vulcan.utility

internal const val KIT_NAME = "Vulcan-Plugin"

internal inline fun taggedLog(tag: String? = null, crossinline lazyMessage: () -> String): () -> String {
    return { "[$KIT_NAME]: ${tag?.let { "($it) " } ?: ""}${lazyMessage()}" }
}

internal fun taggedError(message: String): String =
    taggedLog("Error") { message }()

internal fun taggedRequire(lazyMessage: () -> String): () -> String =
    taggedLog("Require", lazyMessage)

internal fun Boolean.require(lazyMessage: () -> String) {
    require(this, taggedRequire(lazyMessage))
}

internal fun <T> T.log(tag: String? = null) =
    println(taggedLog(tag, ::toString)())

internal fun String.logState() {
    "=".repeat(50).log()
    "| $this  ".log()
    "=".repeat(50).log()

}


private fun main() {
    "This is a log message".log("TestTag")
//    require("This is a require message", "TestTag")
}