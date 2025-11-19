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
            }
        }
    }
}