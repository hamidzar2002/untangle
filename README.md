# Untangle

Native Android starter project for **Untangle**.

Game rules, architecture direction, source references, licensing notes, and the
free-with-ads product model are maintained in [KNOWLEDGEBASE.md](KNOWLEDGEBASE.md).

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- Model-View-Controller (MVC)
- Android Gradle Plugin 9.1.1
- Gradle 9.3.1
- Java 17

## Architecture

- `model/` contains pure Kotlin game state and rules.
- `controller/` owns lifecycle-aware state and handles user actions.
- `view/` contains stateless Compose rendering and gestures.

The view emits actions to the controller; the controller updates the model; the
updated model is rendered by the view.

## Open and run

Open this folder in Android Studio, allow Gradle to sync, and run the `app`
configuration on an emulator or Android device.

From a terminal:

```bash
./gradlew test
./gradlew assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```
