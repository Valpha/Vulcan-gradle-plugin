# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the plugin
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "io.github.VulcanPluginTest"

# Publish to local Maven repository (for testing)
./gradlew publishToMavenLocal

# Publish to local test repo (.repo folder)
./gradlew publishAllPublicationsToLocal-testRepository

# Clean build artifacts
./gradlew clean
```

## Project Overview

Vulcan is a Gradle plugin for Android that simplifies complex build configurations in multi-module projects. It manages modular features and product flavors by defining swappable implementations for different functionalities and combining them into specific application variants.

## Core Architecture

### Plugin Entry Points

- **VulcanPlugin.kt** - Main plugin class that determines if it's applied to root or subprojects and delegates accordingly
- **RootProjectConfiguration.kt** - Configures the root project, creates the `vulcan` extension, resolves module mappings, and configures flavor dimensions
- **SubModuleProjectConfiguration.kt** - Configures submodules, handles variant dimension configuration, and sets up producer/consumer attributes

### Key Concepts

1. **Flavor Dimension**: Represents a feature with multiple implementations (e.g., `payment` dimension with `googlePay` and `wechatPay` implementations)
2. **Flavor**: A specific implementation within a dimension, corresponding to a Gradle module
3. **Variant**: Final application build created from a combination of flavors from different dimensions

### Model Classes (`model/` package)

- **VulcanConfigExtension** - Root extension that holds all configuration: `flavorDimensions`, `variants`, `features`, and `modules`
- **FlavorDimensionConfig** - Defines a dimension containing multiple `Flavor` implementations
- **Flavor** - Individual implementation with `targetModule` and optional `flavorConfig`
- **VariantConfig** - Defines a variant with `flavorMenu` (dimension→flavor mappings) and `featuresMenu`
- **Feature** - Additional feature modules that can be included in variants
- **NamedModuleMapping** - Interface for objects that map to a Gradle `Project`

### Configuration Flow

1. Root project applies Vulcan and creates `VulcanConfigExtension`
2. After evaluation, `resolveFlavorVariantsModuleMapping()` matches dimension/flavor names to actual modules
3. For each `FlavorDimensionConfig`, the plugin configures the target module's Android extension with appropriate flavors
4. Submodules are configured based on their role:
   - **Flavor dimension modules**: Disables unused variants, sets producer attributes
   - **Flavor modules**: Implementation modules (no special config needed)
   - **Consumer modules** (app/features): Configures variant dimension and dependencies

### Utility Classes (`utility/` package)

- **Log.kt** - Tagged logging utilities using Gradle's logging system
- **Attr.kt** - Defines custom Gradle attributes for variant matching (`VulcanAttr`)

## Test Project

The `testProject/` directory contains a working example with:
- Two features (`func1`, `func2`) with multiple implementations
- Example configuration showing dimension/variant setup
- Use it as a reference for understanding plugin behavior

## Publishing

The plugin is configured for publication via Gradle Plugin Portal and custom Maven repositories:
- Plugin ID: `io.github.valpha.vulcan`
- Group: `io.github.valpha.vulcan`
- Version defined in `plugin/build.gradle.kts`
