plugins {
    id("com.android.library") version "8.12.3" apply false
    id("com.android.application") version "8.12.3" apply false
    id("com.valpha.vulcan") version "0.1.0"
}


vulcan {

    flavorDimensions {
        create("ccb") {
            targetModule.set(project("core"))
            flavors {
                create("impl1") {
                    targetModule.set(project("func1-impl1"))
                }
                create("impl2") {
                    targetModule.set(project(":func1-impl2"))
                }
            }
        }
        create("tts") {
            targetModule.set(project("core"))
            flavors {
                create("impl1") {
                    targetModule.set(project("func2-impl1"))
                }
                create("impl2") {
                    targetModule.set(project("func2-impl2"))
                }
            }
        }
    }

    variants {
        create("cc1") {
            targetModule.set(project("app"))
            flavorMenu {
                select("ccb", "impl1")
                select("tts", "impl1")
            }
        }
        create("E624") {
            targetModule.set(project("app"))
            flavorMenu {
                select("ccb", "impl2")
                select("tts", "impl2")
            }
        }
    }
}
