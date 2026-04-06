---
title: Core Concepts
nav_order: 4
---

# Core Concepts

## Stores

Stores are the core business units that manage state for individual widgets. Each Store:

- Manages the UI state for one widget
- Processes actions and updates state accordingly
- Can dispatch side effects to parent layers
- Is easily testable in isolation

A Store is parameterized by two types:

| Type param | Purpose |
|------------|---------|
| `S : UIState` | The state this Store emits |
| `I : StoreInitObj` | Initialization data passed during setup |

Key methods:

```kotlin
override fun initialize(globalModel: I)         // set up initial state
override suspend fun receive(action: StoreAction, storeId: StoreId)  // handle actions
```

State updates use two helpers inside a Store:

```kotlin
emitState { CounterState(count = 0) }      // replace state entirely
updateState { copy(count = count + 1) }     // update current state
```

## Actions

Actions are immutable data classes that describe "what happened". There are three kinds:

| Action type | Handled by | Typical use |
|-------------|------------|-------------|
| `StoreAction` | Store | State changes (increment, load data, toggle) |
| `DataComposerAction` | DataComposer | Cross-store coordination |
| `UIComposerAction` | UI layer | Side effects (navigation, toasts, dialogs) |

Every action carries an `ActionId`. Stores declare which actions they subscribe to via `subscribedStoreAction`:

```kotlin
override val subscribedStoreAction: Set<ActionId> = setOf(
    IncrementActionId,
    DecrementActionId
)
```

## Composers

Composers orchestrate multiple Stores.

**DataComposer** sits between the ViewModel and Stores:
- Manages Store lifecycle
- Routes actions to the correct Store(s) based on `ActionId`
- Combines state from all Stores into a single reactive stream

**UIComposer** sits between the UI (Fragment/Activity/Composable) and the DataComposer:
- Lifecycle-aware state observation
- Dispatches actions from UI events
- Collects side-effect actions (toasts, navigation)

## State

States are immutable data classes implementing `UIState`:

```kotlin
data class CounterState(
    override val widgetId: WidgetId = CounterWidgetId,
    override val visible: Boolean = true,
    override val type: UIStateType = UIStateDefaultType,
    val count: Int = 0
) : UIState
```

Every `UIState` has:
- `widgetId` -- identifies which widget this state belongs to
- `visible` -- whether the widget should be rendered
- `type` -- state type marker (`UIStateDefaultType`, `HeaderUIStateType`, `FooterUIStateType`)

State updates flow reactively through Kotlin `StateFlow` and `SharedFlow`.

## WidgetId

`WidgetId` uniquely identifies a widget within a Composer. It's used for:
- Mapping Stores to widgets via `StoreFactory`
- Filtering states in the UI layer
- Routing actions to the correct Store

```kotlin
object CounterWidgetId : WidgetId {
    override val id: String = "counter_widget"
}
```

## StoreFactory

A `StoreFactory` creates and provides Store instances for each widget:

```kotlin
class CounterStoreFactory : StoreFactory<CounterState, InitModel> {
    override fun get(widgetId: WidgetId): Store<CounterState, InitModel> {
        return when (widgetId) {
            CounterWidgetId -> CounterStore()
            else -> throw IllegalArgumentException("Unknown widget: $widgetId")
        }
    }
}
```
