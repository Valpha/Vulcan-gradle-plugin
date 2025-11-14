package com.valpha.vulcan.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

abstract class NamedModuleMapping {
    @get:Input
    @get:Optional
    abstract val modulePath: Property<String?>

    @get:Internal
    internal lateinit var resolvedProject: Project

    @get:Input
    abstract val targetModule: Property<Project>
}