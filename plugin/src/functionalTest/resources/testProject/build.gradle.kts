
buildscript {
    repositories {
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0") // <- 必须
    }
}

plugins {
    id("com.valpha.vulcan") version "0.1.0"
    id("com.android.library") version "8.6.0" apply false
    id("com.android.application") version "8.6.0" apply false
}



vulcan {
    flavorDimensions {
        create("func1"){
            flavors {
                create("func1-impl1"){
//                    modulePath.
                }
                create("func1-impl2"){

                }
            }
        }
        create("func2") {

        }
    }

    variants{
        create("variant1") {
//            dimensionValue("func1", "value1")
//            dimensionValue("func2", "valueA")
        }
        create("variant2") {
//            dimensionValue("func1", "value2")
//            dimensionValue("func2", "valueB")
        }
    }
}