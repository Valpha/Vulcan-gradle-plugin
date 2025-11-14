plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.func1"
    compileSdk = 34
}
dependencies {
    implementation(project(":func1-impl1"))
    implementation(project(":func1-impl2"))
}
