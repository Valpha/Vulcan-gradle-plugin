package com.fawcar.artifacts.vulcan

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.hasPlugin

class VulcanGradlePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (this == rootProject) {
                configureRootProjectVulcan()
            } else {
                extensions.findByType(AndroidComponentsExtension::class)
                    ?.let {
                        if (plugins.hasPlugin(AppPlugin::class))
                            configureAndroidEntryModule(it)
                        else if (plugins.hasPlugin(LibraryPlugin::class))
                            configureAndroidLibraryModule(it)
                    }
                    ?: false.require(
                        "找不到 AndroidComponentsExtension, 请确认是否将 Vulcan 应用到了 application 或 library 模块上"

                    )
            }
        }
    }
}