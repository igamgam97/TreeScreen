# Plot Wallet Kotlin Multiplatform App - Development Guide

## Build/Lint/Test Commands

- **Build**: `./gradlew assemble<Dev|Staging|Prod>Debug`
- **Check format**: Use [ktlint](https://ktlint.github.io/) with `./gradlew ktlintCheck`
- **Lint**: Use [detekt](https://github.com/detekt/detekt) with `./gradlew detekt`
- **Generate code**: Use [KSP or KAPT](https://github.com/google/ksp) if applicable with `./gradlew kspKotlin<SourceSet>`, otherwise specify tooling/scripts used for codegen.
- **Run all tests**: `./gradlew test`
- **Run specific test**: `./gradlew test --tests *TestClass.testMethod`
- **Run integration tests**: Specify script/command for integration testing if applicable

## Code Style Guidelines

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) and project's detekt rules
- Use MVVM architecture or Clean Architecture as suited
- Legacy code may use other patterns such as MVP - avoid refactoring unless necessary
- Organize imports in the order: Kotlin Standard Libraries → External Libraries → Internal Modules
- Use [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for data models
- Name files with `CamelCase.kt` and classes similarly
- Structure files logically: main classes, utilities, extensions, constants, data classes
- Use descriptive variable names with verbs or nouns (e.g., isLoading, hasError)
- Prefer immutable data structures and functions
- Create small, private classes or functions instead of long methods
- Use [Koin](https://insert-koin.io/) or [Kodein](https://kodein.org/di/) for dependency injection
- Implement navigation using MPP navigation solutions as required
- Write tests for business logic and UI, leveraging shared code as per Kotlin Multiplatform philosophy

## AI Rules Usage Guidelines

When integrating AI or business logic rules in the `ai/rules/` directory, follow these guidelines:

- **app_flow.md**: Reference this to understand user navigation and interaction flow. Update navigation or UI components accordingly by consulting this document.

- **kotlin_guidelines.md**: This acts as the main reference for Kotlin Multiplatform coding standards. Adhere to these when structuring code to ensure compatibility across all platforms.

- **project_structure.md**: Follow this for maintaining consistent code organization across different source sets and platforms. Ensure correct placement of shared and platform-specific code.

- **clean-code.md**: Apply these principles to enhance code quality, focusing on readability, maintainability, security, and clean design patterns.

- **testing_rules.md**: Consult when writing tests. It details strategies for multi-platform testing including unit, integration, and cross-platform synchronization.

### Implementing Features with Plans

1. Follow the steps mentioned strictly.
2. Mark each completed step with "Done" and summarize what was done.
3. Ensure all code meets project guidelines and rules.