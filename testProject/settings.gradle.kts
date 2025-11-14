pluginManagement {
    repositories {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "functionalTest"
include(":app")
include(":core")
include(":func1")
include(":func1-impl1")
include(":func1-impl2")
include(":func2")
include(":func2-impl1")
include(":func2-impl2")
