plugins {
    id("com.android.library") version "8.12.3" apply false
    id("com.android.application") version "8.12.3" apply false
    id("com.valpha.vulcan") version "0.1.0"
}

//dependencies {
//    implementation(gradleKotlinDsl())
//
//    classpath("com.android.tools.build:gradle-api:8.13.0")
//
//}

vulcan {

    flavorDimensions {
        create("func1") {

            flavors {
                create("func1-impl1") {
                    modulePath.set(":func1-impl2")
                }
                create("func1-impl2") {
                    targetModule.set(project(":func1-impl2"))

                }
            }
        }
        create("func2") {
            flavors {
                create("func2-impl1") {
                    targetModule.set(project(":app"))

                }
                create("func2-impl2") {

                }
            }

        }
    }

    variants {
        create("variant1") {
            targetModule.set(project("app"))

//            dimensionValue("func1", "value1")
//            dimensionValue("func2", "valueA")
        }
        create("variant2") {
//            dimensionValue("func1", "value2")
//            dimensionValue("func2", "valueB")
        }
    }


}
