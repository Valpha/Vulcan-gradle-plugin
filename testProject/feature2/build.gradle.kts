plugins {
    id("com.android.library")
}
android {
    namespace = "com.valpha.testproject.feature2"
    compileSdk = 34

}

dependencies {
    implementation(project(":core"))
}
