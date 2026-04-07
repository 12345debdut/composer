# Composer

<div align="center">

**Unidirectional state management for Android**

[![CI](https://github.com/12345debdut/composerlibrary/actions/workflows/ci.yml/badge.svg)](https://github.com/12345debdut/composerlibrary/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.12345debdut/composer)](https://central.sonatype.com/artifact/io.github.12345debdut/composer)
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
    implementation(platform("io.github.12345debdut:composer-bom:1.0.0"))
    implementation("io.github.12345debdut:composer")
    implementation("io.github.12345debdut:composer-compose")  // optional: Compose extensions
    implementation("io.github.12345debdut:composer-fragment") // optional: Fragment helpers
}
```

Published to **Maven Central** -- no extra repository config needed. Composer is a **Kotlin-only** library; Java is not officially supported.

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
class CounterStore : Store<CounterState, InitModel>() {
    override val storeId = CounterStoreId
    override val subscribedStoreAction = setOf(IncrementActionId)

    override fun initialize(globalModel: InitModel) {
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
| `io.github.12345debdut:composer` | Core -- Stores, Actions, Composers, State |
| `io.github.12345debdut:composer-compose` | Jetpack Compose extensions |
| `io.github.12345debdut:composer-fragment` | Fragment base classes for View-based UI |
| `io.github.12345debdut:composer-bom` | BOM -- version alignment for all |

## Publishing (maintainer runbook)

Composer publishes to **Maven Central** via the **Central Publisher Portal**
(`ossrh-staging-api.central.sonatype.com`). The flow is automated by
`.github/workflows/publish.yml`.

### Required GitHub repo secrets

Configure under **Settings → Secrets and variables → Actions**. All five are
mandatory; the workflow's diagnostic step fails fast if any are missing.

| Secret name | Value |
|---|---|
| `MAVEN_CENTRAL_USERNAME` | Central Portal user token **name** (https://central.sonatype.com → Account → Generate User Token) |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal user token **secret** |
| `SIGNING_KEY_ID` | Last 8 hex chars of the GPG key used for signing |
| `SIGNING_PASSWORD` | Passphrase of that GPG key |
| `GPG_PRIVATE_KEY` | Full ASCII-armored private key (`gpg --armor --export-secret-keys KEYID`), including `-----BEGIN/-----END` markers and real newlines |

### Release flow

1. Bump `LIBRARY_VERSION` (and `VERSION_NAME`) in `gradle.properties`
2. Commit and push to `main`
3. Create a GitHub Release with tag `vX.Y.Z` — the tag and `gradle.properties`
   version **must match** or the workflow fails
4. Workflow runs: tests → API check → publish all four modules to staging →
   promote staging into a Central Portal deployment
5. Open https://central.sonatype.com/publishing, find the new deployment,
   click **Publish**

### Modules published

- `io.github.12345debdut:composer`
- `io.github.12345debdut:composer-compose`
- `io.github.12345debdut:composer-fragment`
- `io.github.12345debdut:composer-bom`

### Local publish (dry run to MavenLocal)

```bash
./gradlew \
  :composer:publishReleasePublicationToMavenLocal \
  :composer-compose:publishReleasePublicationToMavenLocal \
  :composer-fragment:publishReleasePublicationToMavenLocal \
  :composer-bom:publishBomPublicationToMavenLocal
```

Local publishing requires `signingInMemoryKey/Id/Password` and
`SONATYPE_USERNAME/PASSWORD` in `~/.gradle/gradle.properties` (never commit).

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
