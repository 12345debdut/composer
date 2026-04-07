---
title: Home
nav_order: 1
---

# Composer Library

A modern, unidirectional state management library for Android applications.

## What is Composer?

Composer provides a structured, scalable approach to managing UI state across multiple widgets and components. It uses a Store-based pattern with reactive state updates via Kotlin Flows.

**Key ideas:**

- **Store-based state management** -- each widget has its own isolated Store
- **Composable architecture** -- multiple Stores compose together seamlessly
- **Action-based communication** -- type-safe actions flow between layers
- **Reactive updates** -- built on Kotlin Flows
- **Unidirectional data flow** -- actions flow down, state flows up
- **Lifecycle-aware** -- automatic lifecycle management for UI observation
- **Testable** -- business logic in Stores is easily testable in isolation

## Modules

| Artifact | Description |
|----------|-------------|
| `io.github.12345debdut:composer` | Core library -- Stores, Actions, Composers, State |
| `io.github.12345debdut:composer-compose` | Jetpack Compose extensions (`collectAsState`, `CollectSideEffect`) |
| `io.github.12345debdut:composer-bom` | BOM -- manages versions for both artifacts |

## Requirements

- Android minSdk 24
- Kotlin 1.9+
- Gradle 8.0+

## Next steps

- [Getting Started](getting-started.md) -- installation and first Store in 7 steps
- [Architecture](architecture.md) -- data flow diagram and component overview
- [Core Concepts](core-concepts.md) -- Stores, Actions, Composers, State
- [Compose Integration](compose.md) -- Jetpack Compose extensions
- [Testing](testing.md) -- testing Stores in isolation
