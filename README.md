# Untangle

Native Android starter project for **Untangle**.

Game rules, architecture direction, source references, licensing notes, and the
free-with-ads product model are maintained in [KNOWLEDGEBASE.md](KNOWLEDGEBASE.md).

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin 9.1.1
- Gradle 9.3.1
- Java 17

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
