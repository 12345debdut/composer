# Composer

<div align="center">

**Unidirectional state management for Android**

[![CI](https://github.com/12345debdut/composerlibrary/actions/workflows/ci.yml/badge.svg)](https://github.com/12345debdut/composerlibrary/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.debdutsaha/composer)](https://central.sonatype.com/artifact/io.github.debdutsaha/composer)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-24+-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](LICENSE)

</div>

Composer is a Store-based state management library for Android. Each widget gets its own Store, actions flow down, state flows up, and everything is observable via Kotlin Flows.

```
  UI  ──action──▶  DataComposer  ──route──▶  Store
  UI  ◀──state──  DataComposer  ◀──emit───  Store
```

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation(platform("io.github.debdutsaha:composer-bom:1.0.0"))
    implementation("io.github.debdutsaha:composer")
    implementation("io.github.debdutsaha:composer-compose") // optional: Compose extensions
}
```

Published to **Maven Central** -- no extra repository config needed.

## Minimal example

```kotlin
// 1. State
data class CounterState(
    override val widgetId: WidgetId = CounterWidgetId,
    override val visible: Boolean = true,
    override val type: UIStateType = UIStateDefaultType,
    val count: Int = 0
) : UIState

// 2. Store
class CounterStore : Store<CounterState, InitModel, WidgetModel>() {
    override val storeId = CounterStoreId
    override val subscribedStoreAction = setOf(IncrementActionId)

    override fun initialise(globalModel: InitModel) {
        emitState { CounterState() }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        if (action is IncrementAction) updateState { copy(count = count + 1) }
    }
}

// 3. Observe (Compose)
@Composable
fun CounterScreen(vm: CounterViewModel) {
    val states by vm.collectAsState()
    val count = states.filterIsInstance<CounterState>().firstOrNull()?.count ?: 0
    Text("$count")
}
```

## Documentation

| Guide | Description |
|-------|-------------|
| [Getting Started](docs/getting-started.md) | Installation and full 7-step walkthrough |
| [Architecture](docs/architecture.md) | Data flow diagram and component overview |
| [Core Concepts](docs/core-concepts.md) | Stores, Actions, Composers, State |
| [Compose Integration](docs/compose.md) | `collectAsState()`, `CollectSideEffect()` |
| [Testing](docs/testing.md) | Testing Stores in isolation |
| [Publishing](PUBLISHING.md) | Publishing to Maven Central |
| [Troubleshooting](TROUBLESHOOTING.md) | Common issues and solutions |
| [Changelog](CHANGELOG.md) | Version history |

## Artifacts

| Artifact | Purpose |
|----------|---------|
| `io.github.debdutsaha:composer` | Core -- Stores, Actions, Composers, State |
| `io.github.debdutsaha:composer-compose` | Jetpack Compose extensions |
| `io.github.debdutsaha:composer-bom` | BOM -- version alignment for both |

## Contributing

Contributions welcome. Please open an issue before starting major changes.

1. Fork the repo
2. Create a feature branch
3. Make your changes (ensure `./gradlew build` and `./gradlew apiCheck` pass)
4. Open a Pull Request

See [Contributing Guide](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md).

## License

```
Copyright 2024 Debdut Saha

Licensed under the Apache License, Version 2.0
```

See [LICENSE](LICENSE) for the full text.
