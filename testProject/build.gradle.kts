plugins {
    id("com.android.library") version "8.12.3" apply false
    id("com.android.application") version "8.12.3" apply false
    id("com.valpha.vulcan") version "0.1.0"
}


vulcan {

    flavorDimensions {
        create("func1") {
            targetModule.set(project("core"))
            flavors {
                create("func1_1") {
                    targetModule.set(project("func1-impl1"))
                }
                create("func1_2") {
                    targetModule.set(project(":func1-impl2"))
                    flavorConfig {
                        isDefault = true
                    }
                }
            }
        }
        create("func2") {
            targetModule.set(project("core"))
            flavors {
                create("func2_1") {
                    targetModule.set(project("func2-impl1"))
                }
                create("func2_2") {
                    targetModule.set(project("func2-impl2"))
                }
                create("func2_3") {
                    targetModule.set(project("func2-impl3"))
                }
            }
        }
    }

    variants {
        create("v12") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_1")
                select("func2", "func2_2")
            }
        }
        create("v23") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_2")
                select("func2", "func2_3")
            }
        }
    }
}
