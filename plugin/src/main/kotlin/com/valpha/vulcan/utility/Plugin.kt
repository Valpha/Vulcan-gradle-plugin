package com.valpha.vulcan.utility

import com.android.build.gradle.BasePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.hasPlugin

internal fun checkProjectHasAndroidPlugin(project: Project) {
    project.plugins.hasPlugin(BasePlugin::class).require {
        "Target Module [${project.displayName}] must be an Android Module (apply 'com.android.library' or 'com.android.application' plugin)!"
    }
}