plugins {
    id("com.android.application")
}

android{
    namespace = "com.valpha.testproject.app"

    compileSdk = 34

}
dependencies {
    implementation(project(":feature1"))
    implementation(project(":feature2"))
}
