package com.valpha.vulcan.model

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

interface NamedModuleMapping {
    @get:Input
     val targetModule: Property<Project>
}