package io.github.valpha

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A simple 'hello world' plugin.
 */
class VulcanPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // Register a task
            tasks.register("greeting") {
                doLast {
                    println("Hello from plugin 'com.valpha.greeting'")
                }
            }



            if (this == rootProject) {
                configureRootProjectVulcan()
                rootProject.subprojects {
                    configureSubModuleProjectVulcan()
                }
            } else {
//                extensions.findByType(AndroidComponentsExtension::class)
//                    ?.let {
//                        if (plugins.hasPlugin(AppPlugin::class))
//                            configureAndroidEntryModule(it)
//                        else if (plugins.hasPlugin(LibraryPlugin::class))
//                            configureAndroidLibraryModule(it)
//                    }
//
//                    ?: require(false) {
//                        taggedRequire { "找不到 AndroidComponentsExtension, 请确认是否将 Vulcan 应用到了 application 或 library 模块上" }
//                    }
            }
        }
    }
}