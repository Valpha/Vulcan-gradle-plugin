# Vulcan Gradle Plugin

Vulcan is an Android Gradle plugin designed to simplify complex build configurations in multi-module projects. It empowers you to manage modular features and product flavors by defining swappable implementations for different functionalities and combining them into specific application variants.

## 🤔 The Challenge of Modular Architectures

In modern Android development using Clean Architecture, it's common for a single feature (represented by an interface) to require multiple implementations for different platforms or distribution channels. As a project scales, modularization becomes a necessity, but it introduces significant challenges:

*   **Complex Configuration:** Manually managing and switching dependencies for implementation modules across different build variants is tedious and error-prone.
*   **Cluttered Build Scripts:** `build.gradle` files become bloated with conditional logic (`if/else` blocks) to handle variant-specific dependencies, making them difficult to read and maintain.
*   **Poor Scalability:** Adding a new feature implementation or product channel often requires modifying build logic in multiple places, resulting in extensive rework.

Vulcan addresses these pain points head-on. It abstracts the **interface-implementation** relationship into the more intuitive concept of **FlavorDimension-Flavor**. Think of a **Variant** as the final dish you're serving, and **Flavors** as the customizable ingredients you choose for it (e.g., "spicy" or "mild").

By centralizing this logic in your root `build.gradle.kts`, Vulcan streamlines the setup of multi-module projects, making your build logic clearer, more robust, and easier to scale.

## ✨ Features

*   **Modular Feature Switching:** Define features as "Flavor Dimensions" and provide multiple implementation modules for each.
*   **Variant Combination:** Effortlessly combine implementations from different dimensions to create final application variants.
*   **Centralized Configuration:** Manage variant-specific properties like `applicationId` and `versionCode` from a single location.
*   **Concise Kotlin DSL:** A clean, declarative DSL for configuring your build logic.
*   **Clean Dependency Management:** Eliminate conditional dependency logic from your `build.gradle` files.

## 🚀 Quick Start

Apply the plugin in your project's root `build.gradle.kts` file:

```kotlin
// build.gradle.kts
plugins {
    id("io.github.valpha.vulcan") version "1.0"
}
```

## ⚙️ How to Use

### Core Concepts

*   **Flavor Dimension:** Represents a feature in your app that has multiple implementations (e.g., a `payment` dimension with `googlePay` and `wechatPay` implementations).
*   **Flavor:** A specific implementation within a dimension. Each flavor typically corresponds to a separate Gradle module (e.g., `googlePay`).
*   **Variant:** The final application build, created from a specific combination of flavors from different dimensions.

### Practical Example: The `testProject`

Let's break down how Vulcan is used in the `testProject` to see these concepts in action.

**1. Module Structure**

The project has two main features, `func1` and `func2`, each with multiple implementation modules:
```
testProject/
├── app/                # Main application module
├── core/               # Core module for common logic or interfaces
├── func1-impl1/        # Implementation A for func1
├── func1-impl2/        # Implementation B for func1
├── func2-impl1/        # Implementation A for func2
├── func2-impl2/        # Implementation B for func2
└── func2-impl3/        # Implementation C for func2
```

**2. Vulcan Configuration**

In the root `build.gradle.kts`, we configure Vulcan to map features to their implementations and combine them into variants:

```kotlin
// build.gradle.kts
vulcan {
    // 1. Define flavor dimensions and their available flavors (implementations)
    flavorDimensions {
        create("func1") {
            flavors {
                create("func1_1") { targetModule.set(project("func1-impl1")) }
                create("func1_2") {
                    targetModule.set(project(":func1-impl2"))
                    flavorConfig { isDefault = true }
                }
            }
        }
        create("func2") {
            flavors {
                create("func2_1") { targetModule.set(project("func2-impl1")) }
                create("func2_2") {
                    targetModule.set(project("func2-impl2"))
                    flavorConfig { isDefault = true }
                }
                create("func2_3") { targetModule.set(project("func2-impl3")) }
            }
        }
    }

    // 2. Define application variants by combining flavors
    variants {
        create("v12") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_1") // v12 uses implementation 1 of func1
                select("func2", "func2_2") // v12 uses implementation 2 of func2
            }
            flavorConfig {
                applicationId = "com.valpha.vulcan.v12"
                versionCode = 40
            }
        }
        create("v23") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_2") // v23 uses implementation 2 of func1
                select("func2", "func2_3") // v23 uses implementation 3 of func2
            }
            flavorConfig {
                applicationId = "com.valpha.vulcan.v23"
                versionCode = 188
            }
        }
    }
}
```

**3. How It Works**

*   **Dimension-to-Implementation Mapping:** The `flavorDimensions` block maps an abstract feature (like `func1`) to its concrete implementation modules (like `:func1-impl1`).
*   **Variant-to-Flavor Combination:** The `variants` block defines the final app builds. For instance, the `v12` variant is a combination of the `func1_1` and `func2_2` flavors.

When a developer selects the `v12Debug` build variant in Android Studio and syncs the project, Vulcan dynamically configures the `app` module's dependencies:

```kotlin
// Dependencies for the 'app' module under the v12Debug variant
implementation(project(":func1-impl1"))
implementation(project(":func2-impl2"))
```

Switching to `v23Debug` automatically changes the dependencies:

```kotlin
// Dependencies for the 'app' module under the v23Debug variant
implementation(project(":func1-impl2"))
implementation(project(":func2-impl3"))
```

This approach effectively decouples the main application from its feature implementations, centralizing the complex dependency-switching logic into a single, clean configuration block.

## 🤝 Contributing

Contributions of all kinds are welcome! Please feel free to open a Pull Request with your ideas and suggestions.

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.
