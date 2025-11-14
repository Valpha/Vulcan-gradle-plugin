plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.core"

    compileSdk = 34
}
dependencies {
    implementation(project(":func1"))
    implementation(project(":func2"))
}
