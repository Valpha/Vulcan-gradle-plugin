package com.fawcar.artifacts.vulcan

import com.android.build.api.dsl.ApplicationProductFlavor

data class AppFlavor(
    val name: String,
    val selectedFlavors: Set<String>,
    val configuration: (ApplicationProductFlavor.() -> Unit)? = null
)

fun MutableList<AppFlavor>.appFlavor(
    name: String,
    selectedFlavors: Set<String>,
    configuration: (ApplicationProductFlavor.() -> Unit)? = null
) = add(AppFlavor(name, selectedFlavors, configuration))
