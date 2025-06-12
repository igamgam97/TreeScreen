# Rijksmuseum - Kotlin Multiplatform Development Guide

## Project Overview
Rijksmuseum is a multi-modular Kotlin and Compose Multiplatform app that offers an immersive way to explore the art collection of the renowned Rijksmuseum in Amsterdam. The app is built to run on multiple platforms:
- Android
- iOS

## Build/Run Commands

- Build Android: `./gradlew assembleDebug`
- Run Android: `./gradlew :composeApp:installDebug`

## Project Requirements

- JDK 17+
- Android Studio / IntelliJ IDEA
- Android SDK
- Xcode (for iOS builds)

## Architecture Overview

The project follows a Clean Architecture approach with a modular structure:

### Module Structure
```
- build-logic  
- composeApp (Main application module: entry point, navigation host)

- core  
  - common (Shared utilities, base components, and DI setup used between features)  
  - data (Shared data sources and repository implementations used between features)  
  - designsystem (Reusable UI components and theming shared across the app)  
  - domain (Shared business logic and use cases used between features)  
  - model (Shared data models/entities used across layers and features)  
  - network (API setup, clients, interceptors, and serialization logic)  
  - permissions (Generic permission handling logic)

- feature  
  - arts (Art gallery feature)
    - presentation (UI screens, ViewModels)
    - domain (Feature-specific repository interfaces and use cases)
    - data (Feature-specific data sources and repository implementations)

  - detail (Art detail feature)
    - presentation (UI screens, ViewModels)
    - domain (Feature-specific repository interfaces and use cases)
    - data (Feature-specific data sources and repository implementations)
```

## Key Libraries and Technologies

- **UI**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **DI**: [Koin](https://insert-koin.io/) for dependency injection
- **Networking**: [Ktor](https://ktor.io/) for HTTP requests
- **Image Loading**: [Coil3](https://coil-kt.github.io/coil/) with Ktor network integration
- **Serialization**: [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON parsing
- **Async**: [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for asynchronous programming
- **Navigation**: Compose Navigation
- **Logging**: [Kermit](https://github.com/touchlab/Kermit) for multiplatform logging
- **Image Processing**: [zoomimage-compose-coil](https://github.com/panpf/zoomimage) for zoomable images

## Architecture Patterns

### MVVM Architecture
- **ViewModels**: Uses Jetpack ViewModels with Kotlin flows for state management
- **UI State**: Represented as sealed interfaces/classes
- **Events**: User actions modeled as sealed classes
- **Data Flow**: Unidirectional data flow from ViewModel to UI

### Clean Architecture
- **Domain Layer**: Use cases for business logic
- **Data Layer**: Repositories for data access
- **Presentation Layer**: ViewModels and Compose UI

### Error Handling
- Uses kotlin result pattern for error handling
- ApiResponse sealed interface for network error types
- Consistent error mapping from network to UI

## Code Style Guidelines

### General Structure
- Follow Kotlin coding conventions
- Organize imports: Kotlin/JDK → Android/Platform → Third-party → Internal
- Group related functionality in modules
- Keep UI components small and focused

### Platform-Specific Code
- Use `expect/actual` pattern for platform-specific implementations
- Keep platform-specific code in appropriate sourcesets:
  - `commonMain`: Shared code for all platforms
  - `androidMain`: Android-specific code
  - `iosMain`: iOS-specific code

### Compose UI Guidelines
- Use Compose functions with clear parameter grouping
- Extract reusable components to separate composable functions
- Follow state hoisting principles
- Keep UI components small and focused on a single responsibility

### File Organization
- One class per file for major components
- Group related smaller components in a single file
- Place platform-specific implementations in the appropriate sourceset

## Contributing Guidelines

1. Follow existing architecture and patterns
2. Keep modules focused on their responsibility
3. Write unit tests for business logic
4. Use appropriate error handling
5. Document public APIs and complex logic
6. Follow existing code style and formatting

## Testing

- Unit tests for domain and data layer
- UI tests for key user flows
- Run tests: `./gradlew test`

## Common Issues and Solutions

- **iOS Build Failures**: Run `./gradlew :composeApp:podInstall` to update CocoaPods
- **Network API Issues**: Verify API key is correctly set in secrets.properties
- **Build Errors**: Ensure Gradle and Kotlin plugin versions are compatible