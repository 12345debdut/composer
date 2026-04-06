# Troubleshooting

Common issues and their solutions when using the Composer library.

---

## Dependency Resolution Issues

**Symptom:** Gradle fails to resolve `io.github.debdutsaha:composer` or reports version conflicts.

**Cause:** Maven Central repository is not configured, or a cached stale version is being used.

**Fix:**
1. Ensure `mavenCentral()` is in your `repositories` block (it is by default in new projects).
2. If using the BOM, make sure it is declared with `platform()`:
   ```kotlin
   implementation(platform("io.github.debdutsaha:composer-bom:1.0.0"))
   implementation("io.github.debdutsaha:composer")
   ```
3. Run `./gradlew --refresh-dependencies` to clear the dependency cache.

---

## ProGuard / R8

**Symptom:** `ClassNotFoundException` or reflection-related crashes in release builds.

**Cause:** Composer's consumer ProGuard rules should be picked up automatically. If they are not, your build configuration may be stripping them.

**Fix:**
Add these rules to your app's `proguard-rules.pro`:
```
-keep class com.debdut.composer.** { *; }
-keep interface com.debdut.composer.** { *; }
```

---

## Compose Version Mismatch

**Symptom:** Compilation errors in `composer-compose` referencing missing Compose APIs.

**Cause:** Your project's Compose BOM version may be incompatible with the one `composer-compose` was built against.

**Fix:**
Align your Compose BOM with the version used by this library. Check `gradle/libs.versions.toml` in this repository for the exact `composeBom` version, and use the same (or a compatible) BOM in your project:
```kotlin
implementation(platform("androidx.compose:compose-bom:2024.12.01"))
```

---

## State Not Updating

**Symptom:** UI does not reflect state changes after dispatching an action.

**Cause:** Common misuse of `emitState` vs `updateState` inside a `Store`.

**Fix:**
- Use `emitState { MyState(...) }` to set the **initial** state (replaces any existing state).
- Use `updateState { copy(field = newValue) }` to **transform** the current state.
- `updateState` returns `null` and does nothing if the store has not been initialised yet. Make sure `emitState` is called in `initialise()` before any `updateState` calls.
- Verify the action's `actionId` is included in the store's `subscribedStoreAction` set. Actions whose ID is not in the set are silently ignored.

---

## Build Errors

### `Explicit API mode` violations

**Symptom:** Compilation error `Visibility must be specified in explicit API mode`.

**Cause:** If you are contributing to the library itself, all public declarations require an explicit `public` or `internal` visibility modifier.

**Fix:** Add `public` or `internal` to the declaration. If it is internal implementation detail, use `internal`.

### JDK version mismatch

**Symptom:** `Unsupported class file major version` or Kotlin/JVM target errors.

**Cause:** The project targets JVM 11. Your local JDK may be older.

**Fix:** Install JDK 11 or later (JDK 21 recommended). In Android Studio: **File > Project Structure > SDK Location > JDK Location**.

---

## Still stuck?

Open an issue on GitHub: [New Issue](https://github.com/12345debdut/composerlibrary/issues/new/choose)
