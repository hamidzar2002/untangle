# Untangle

Native Android game for **Untangle**.

Drag nodes until no connecting lines cross. Solving a puzzle opens a
congratulations dialog and advances to a harder level with another node and
more edges. Players can choose a starting size from 4 to 24 nodes; progression
continues up to 30 nodes.

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

## Game loop

- Generated planar puzzles with guaranteed crossing-free solutions
- Randomly scattered starting layouts that always contain crossings
- Level progression with increasing nodes and connections
- Completion dialog and next-level navigation
- Starting node-count selector, new puzzle, restart, and move counter
- Responsive portrait/landscape board that prioritizes the playable area
- Large immediate drag targets and single-pass crossing analysis
- Built-in help page and persistent sound/mute control
- Matching Google Play and adaptive Android launcher icons

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
