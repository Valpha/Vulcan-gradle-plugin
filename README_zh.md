# Vulcan Gradle Plugin

Vulcan 是一个为 Android 设计的 Gradle 插件，旨在简化多模块项目中的复杂构建配置。它通过管理模块化功能和产品渠道（Product Flavors），允许您为不同功能定义可互换的实现，并将它们组合成特定的应用程序变体。

## 🤔 背景：模块化架构的挑战

在采用整洁架构（Clean Architecture）的现代 Android 开发中，同一个功能（接口）常常需要为不同的平台或渠道提供多种实现。随着项目规模扩大，模块化成为必然选择，但这也带来了新的挑战：

*   **配置繁琐：** 为不同的构建变体（Variant）手动管理和切换实现模块的依赖，过程复杂且容易出错。
*   **构建脚本混乱：** `build.gradle` 文件中充斥着大量 `if/else` 判断来处理变体依赖，导致脚本难以维护。
*   **扩展性差：** 每当增加一个新的功能实现或产品渠道，都需要多处修改构建逻辑，工作量巨大。

Vulcan 的诞生正是为了解决这些痛点。它将 **接口-实现** 的关系抽象为 **风味维度-风味 (FlavorDimension-Flavor)** 的概念。您可以将 **变体 (Variant)** 想象成一道最终的菜品，而 **风味 (Flavor)** 则是为这道菜选择的定制项（例如“辣”或“不辣”）。

通过在根 `build.gradle.kts` 中进行集中化配置，Vulcan 极大地简化了多模块项目的配置工作，让构建逻辑更加清晰、健壮且易于扩展。

## ✨ 特性

*   **模块化功能切换:** 通过“渠道维度（Flavor Dimensions）”定义不同功能，并为其提供多种实现模块。
*   **变体组合:** 自由组合来自不同维度的实现，创建最终的应用程序变体。
*   **集中化配置:** 在单一位置配置变体专属属性，如 `applicationId`、`versionCode` 等。
*   **简洁的 DSL:** 提供直观的 Kotlin DSL 来声明式地配置构建逻辑。
*   **清晰的依赖管理:** 避免在 `build.gradle` 文件中使用条件逻辑来管理依赖。

## 🚀 快速开始

在您根项目的 `build.gradle.kts` 文件中应用本插件：

```kotlin
// build.gradle.kts
plugins {
    id("io.github.valpha.vulcan") version "1.0"
}
```

## ⚙️ 使用指南

### 核心概念

*   **Flavor Dimension (渠道维度):** 代表应用中一个具有多种实现的功能。例如，`payment` 维度可以有 `googlePay` 和 `wechatPay` 两种实现。
*   **Flavor (渠道):** 维度下的一个具体实现。例如，`googlePay` 是一个 Flavor，通常对应一个独立的 Gradle 模块。
*   **Variant (变体):** 一个最终的应用程序构建形态，由来自不同维度的特定 Flavor 组合而成。

### 实践示例：testProject

为了更好地理解 Vulcan 的工作方式，让我们看一下 `testProject` 是如何组织和配置的。

**1. 模块结构**

`testProject` 的模块结构如下，其中 `func1` 和 `func2` 是两大功能，各自拥有多个实现模块：
```
testProject/
├── app/                # 主应用模块
├── core/               # 核心模块，包含通用逻辑或接口定义
├── func1-impl1/        # 功能1的实现A
├── func1-impl2/        # 功能1的实现B
├── func2-impl1/        # 功能2的实现A
├── func2-impl2/        # 功能2的实现B
└── func2-impl3/        # 功能2的实现C
```

**2. Vulcan 配置**

在根 `build.gradle.kts` 中，我们按如下方式配置 Vulcan：

```kotlin
// build.gradle.kts
vulcan {
    // 定义渠道维度及其包含的渠道
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

    // 通过组合不同的渠道来定义应用程序变体
    variants {
        create("v12") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_1") // v12 变体使用 func1 的 impl1 实现
                select("func2", "func2_2") // v12 变体使用 func2 的 impl2 实现
            }
            flavorConfig {
                applicationId = "com.valpha.vulcan.v12"
                versionCode = 40
            }
        }
        create("v23") {
            targetModule.set(project("app"))
            flavorMenu {
                select("func1", "func1_2") // v23 变体使用 func1 的 impl2 实现
                select("func2", "func2_3") // v23 变体使用 func2 的 impl3 实现
            }
            flavorConfig {
                applicationId = "com.valpha.vulcan.v23"
                versionCode = 188
            }
        }
    }
}
```

**3. 工作机制与效果**

*   **维度与实现映射:** `flavorDimensions` 配置块将抽象的功能（如 `func1`）与其具体的实现模块（如 `func1-impl1`）关联起来。
*   **变体与风味组合:** `variants` 配置块定义了最终的应用变体。例如，`v12` 变体被定义为 `func1_1` 和 `func2_2` 这两个风味的组合。

当开发者在 Android Studio 中选择 `v12Debug` 作为构建变体并同步项目时，Vulcan 会动态地为 `app` 模块配置以下依赖：

```kotlin
// v12Debug 变体下的 app 模块依赖
implementation(project(":func1-impl1"))
implementation(project(":func2-impl2"))
```

如果切换到 `v23Debug`，依赖则会自动变为：

```kotlin
// v23Debug 变体下的 app 模块依赖
implementation(project(":func1-impl2"))
implementation(project(":func2-impl3"))
```

通过这种方式，Vulcan 将复杂的依赖切换逻辑从各模块的 `build.gradle` 文件中解放出来，集中到了根配置中，实现了清晰、可维护的平台化项目组织。

## 🤝 贡献

欢迎各种形式的贡献！如果您有任何想法或建议，请随时提交 Pull Request。

## 📄 许可证

本项目采用 MIT 许可证。有关详细信息，请参阅 `LICENSE` 文件。
