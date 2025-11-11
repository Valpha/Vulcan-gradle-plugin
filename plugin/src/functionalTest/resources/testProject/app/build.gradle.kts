plugins {
    id("com.android.application")
}

android{
    namespace = "com.valpha.testproject.app"

    compileSdk = 34
}
dependencies {
    implementation(":core")
}
