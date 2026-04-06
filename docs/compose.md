---
title: Compose Integration
nav_order: 5
---

# Jetpack Compose Integration

The `composer-compose` artifact provides first-class Jetpack Compose support via extension functions on `DataComposerHost`.

## Installation

```kotlin
dependencies {
    implementation(platform("io.github.debdutsaha:composer-bom:1.0.0"))
    implementation("io.github.debdutsaha:composer")
    implementation("io.github.debdutsaha:composer-compose")
}
```

## Observing state

Use `collectAsState()` to observe Store states as Compose `State`:

```kotlin
import com.debdut.composer.compose.collectAsState

@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val states by viewModel.collectAsState()

    val count = states.filterIsInstance<CounterState>().firstOrNull()?.count ?: 0

    Text("Count: $count")
}
```

`collectAsState()` is lifecycle-aware -- it pauses collection when the lifecycle drops below `STARTED` and resumes automatically.

## Handling side effects

Use `CollectSideEffect` for one-shot UI actions like toasts, navigation, or dialogs:

```kotlin
import com.debdut.composer.compose.CollectSideEffect

@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val context = LocalContext.current

    CollectSideEffect(viewModel) { holder ->
        when (val action = holder.action) {
            is ShowToastAction -> {
                Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ... rest of UI
}
```

## Dispatching actions

Actions are dispatched through the `container`:

```kotlin
Button(onClick = { viewModel.container.dispatch(IncrementAction()) }) {
    Text("Increment")
}
```

## Full example

```kotlin
@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val states by viewModel.collectAsState()
    val context = LocalContext.current
    val counterState = states.filterIsInstance<CounterState>().firstOrNull()

    CollectSideEffect(viewModel) { holder ->
        when (val action = holder.action) {
            is ShowToastAction -> {
                Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${counterState?.count ?: 0}",
            style = MaterialTheme.typography.displayLarge
        )

        Row {
            Button(onClick = { viewModel.container.dispatch(DecrementAction()) }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { viewModel.container.dispatch(IncrementAction()) }) {
                Text("+")
            }
        }
    }
}
```

## API reference

| Extension | Description |
|-----------|-------------|
| `host.collectAsState()` | Lifecycle-aware `State<List<UIState>>` -- recomposes on state changes, pauses below `STARTED` |
| `host.collectAsStateNoLifecycle()` | Same but without lifecycle awareness -- use in previews or tests |
| `CollectSideEffect(host) { }` | Collects `UIComposerAction` side effects in a `LaunchedEffect` |
| `host.uiStateFlow` | Direct access to the underlying `StateFlow<List<UIState>>` |
| `host.uiActionFlow` | Direct access to the underlying `SharedFlow<UIComposerActionHolder>` |
