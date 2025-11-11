plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.func1"
    compileSdk = 34
}
dependencies {
    implementation(":func1-impl1")
    implementation(":func1-impl2")
}
