plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.0"
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    jvmToolchain(17)
}

group = "com.fawcar.artifacts"
val PREDEFINED_VERSION = "0.1.4"

// 在发布的时候，会写入publishVersion这个Property中版本号，实际上加不加-SNAPSHOT 不影响CI发布，会自动根据CI类型确定版本号
version = findProperty("build.publish.version") ?: PREDEFINED_VERSION


dependencies {
    compileOnly("com.android.tools.build:gradle-api:8.7.3")
    implementation(gradleKotlinDsl())
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}

tasks.validatePlugins {
    enableStricterValidation = true
    failOnWarning = true
}

gradlePlugin {
    plugins {
        create("vulcan") {
            id = "com.fawcar.artifacts.vulcan"
            implementationClass = "com.fawcar.artifacts.vulcan.VulcanGradlePlugin"
            displayName = "Vulcan Plugin"
            description = "【座舱中心】平台化配置插件"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "gitlab"
            url = uri("${System.getenv("CI_API_V4_URL")}/projects/${System.getenv("CI_PROJECT_ID")}/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = "Job-Token"
                value = findProperty("deployToken") as String? // 属性在CI中配置，本地不做配置
                isAllowInsecureProtocol = true
            }
            authentication {
                create("header", HttpHeaderAuthentication::class)
            }
        }
        maven {
            name = "local-test"
            url = uri("./repo")
        }
    }
}