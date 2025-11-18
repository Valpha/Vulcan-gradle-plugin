package io.github.valpha.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface NamedModuleMapping {
    @get:Input
     val targetModule: Property<Project>
}