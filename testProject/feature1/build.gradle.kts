plugins {
    id("com.android.library")
}
android{
    namespace = "com.valpha.testproject.feature1"
    compileSdk = 34

}

dependencies {
    implementation(project(":core"))
}
