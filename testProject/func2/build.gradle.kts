plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.func2"
    compileSdk = 34
}
dependencies {
    implementation(project(":func2-impl1"))
    implementation(project(":func2-impl2"))
}
