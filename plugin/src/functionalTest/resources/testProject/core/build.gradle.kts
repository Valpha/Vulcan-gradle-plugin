plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.core"

    compileSdk = 34
}
dependencies {
    implementation(":func1")
    implementation(":func2")
}
