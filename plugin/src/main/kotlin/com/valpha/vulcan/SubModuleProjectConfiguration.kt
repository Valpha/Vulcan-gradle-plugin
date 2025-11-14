package com.valpha.vulcan

import com.valpha.vulcan.model.VulcanConfigExtension
import com.valpha.vulcan.utility.log
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureSubModuleProjectVulcan() {
    this.displayName.log("setup")

    // add module name to root vulcan config
    getRootVulcanConfig().modules += this
}

private fun Project.getRootVulcanConfig() = rootProject.extensions.getByType<VulcanConfigExtension>()

