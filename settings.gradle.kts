pluginManagement {
    repositories {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
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
rootProject.name = "vulcan"

