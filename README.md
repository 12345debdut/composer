# StoreComposer Module Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Module Structure](#module-structure)
4. [Core Concepts](#core-concepts)
5. [File Documentation](#file-documentation)
6. [Usage Examples](#usage-examples)
7. [Best Practices](#best-practices)

---

## Overview

The **StoreComposer** module is a state management library that implements a unidirectional data flow architecture for Android applications. It provides a structured way to manage UI state across multiple widgets/components with support for:

- **Store-based state management**: Each widget has its own Store that manages its state
- **Composable architecture**: Multiple stores can be composed together
- **Action-based communication**: Actions flow between UI, Stores, and Composers
- **Reactive state updates**: Uses Kotlin Flows for reactive state propagation

### Key Benefits
- Decoupled widget state management
- Predictable state updates through actions
- Easy testing of business logic
- Scalable architecture for complex screens with multiple widgets

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI Layer                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  Fragment/Activity                                          ││
│  │  (ListUIComposerFragment / ListUIComposerWithHeaderFooter)  ││
│  └─────────────────────────────────────────────────────────────┘│
│                              │                                   │
│                              ▼                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  UIComposer                                                 ││
│  │  (Observes state and dispatches UI actions)                 ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Data Composer Layer                          │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  DataComposer (Single/List/ListWithHeaderFooter)            ││
│  │  - Manages multiple Stores                                  ││
│  │  - Routes actions to appropriate Stores                     ││
│  │  - Combines state from all Stores                           ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Store Layer                               │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐   │
│  │  Store 1   │ │  Store 2   │ │  Store 3   │ │  Store N   │   │
│  │            │ │            │ │            │ │            │   │
│  │ - State    │ │ - State    │ │ - State    │ │ - State    │   │
│  │ - Actions  │ │ - Actions  │ │ - Actions  │ │ - Actions  │   │
│  │ - StoreId  │ │ - StoreId  │ │ - StoreId  │ │ - StoreId  │   │
│  └────────────┘ └────────────┘ └────────────┘ └────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **User Interaction** → UI dispatches `StoreAction`
2. **Action Routing** → DataComposer routes action to appropriate Store(s)
3. **State Update** → Store processes action and updates its state
4. **State Emission** → New state flows back through DataComposer
5. **UI Update** → UIComposer receives combined state and renders UI

---

## Module Structure

```
storecomposer/
├── action/                          # Action definitions
│   ├── Action.kt                    # Base action interface
│   ├── ActionId.kt                  # Action identifier interface
│   ├── ComposerAction.kt            # Composer-level actions
│   ├── DataComposerAction.kt        # Data composer actions
│   ├── StoreAction.kt               # Store-level actions
│   ├── UIComposerAction.kt          # UI composer actions
│   └── holder/                      # Action wrappers
│       ├── ActionHolder.kt          # Base action holder
│       ├── DataComposerActionHolder.kt
│       ├── StoreActionHolder.kt
│       └── UIComposerActionHolder.kt
│
├── composer/                        # Composer implementations
│   ├── Composer.kt                  # Base composer interface
│   ├── data/                        # Data composers
│   │   ├── DataComposer.kt          # Base data composer
│   │   ├── DataComposerActionHandler.kt
│   │   ├── SingleDataComposer.kt
│   │   ├── ListDataComposer.kt
│   │   ├── ListWithHeaderAndFooterDataComposer.kt
│   │   ├── host/                    # Composer hosts
│   │   │   ├── DataComposerHost.kt
│   │   │   ├── SingleDataComposerHost.kt
│   │   │   ├── ListDataComposerHost.kt
│   │   │   └── ListWithHeaderAndFooterDataComposerHost.kt
│   │   ├── impl/                    # Implementations
│   │   │   ├── SingleDataComposerImpl.kt
│   │   │   ├── ListDataComposerImpl.kt
│   │   │   ├── ListWithHeaderFooterDataComposerImpl.kt
│   │   │   └── IntermediateWidgetStoreModel.kt
│   │   ├── model/
│   │   │   └── StoreActionWidgetIdPair.kt
│   │   └── syntax/                  # Extension functions
│   │       ├── DataComposerHostSyntax.kt
│   │       ├── SingleDataComposerHostSyntax.kt
│   │       └── ListWithHeaderAndFooterDataComposerHostSyntax.kt
│   └── ui/                          # UI composers
│       ├── UIComposer.kt
│       ├── SingleUIComposer.kt
│       ├── ListUIComposer.kt
│       ├── ListUIComposerWithHeaderAndFooter.kt
│       ├── WidgetId.kt
│       ├── HostWidgetId.kt
│       ├── ChildWidgetId.kt
│       ├── NoStoreWidgetId.kt
│       ├── GroupWidget.kt
│       └── syntax/
│           ├── UIComposerSyntax.kt
│           └── ListUIComposerWithHeaderAndFooterSyntax.kt
│
├── extensions/                      # Helper extensions
│   ├── CoroutineExtensions.kt       # Coroutine utilities
│   └── HostExtensions.kt            # Factory functions
│
├── state/                           # State definitions
│   ├── UIState.kt                   # Base UI state interface
│   ├── UIStateType.kt               # State type markers
│   ├── HeaderUIStateType.kt         # Header state marker
│   ├── FooterUIStateType.kt         # Footer state marker
│   └── NoOpsUIState.kt              # No-op state implementation
│
├── store/                           # Store layer
│   ├── Store.kt                     # Base store class
│   ├── StoreId.kt                   # Store identifier
│   ├── StoreInitObj.kt              # Initialization object
│   ├── StoreWidgetModel.kt          # Widget model interface
│   ├── factory/
│   │   └── StoreFactory.kt          # Store factory interface
│   └── syntax/
│       └── StoreSyntax.kt           # Store extension functions
│
├── uicomponents/                    # UI components
│   ├── ListUIComposerFragment.kt
│   └── ListUIComposerWithHeaderAndFooterFragment.kt
│
└── viewmodel/                       # ViewModel implementations
    ├── ListDataComposerViewModel.kt
    └── ListWithHeaderAndFooterDataComposerViewModel.kt
```

---

## Core Concepts

### 1. Actions
Actions are the primary way to communicate between layers. They are immutable data classes that describe "what happened".

### 2. Stores
Stores are self-contained business units that manage the state for a single widget. Each Store has:
- A unique `StoreId`
- A `UIState` it manages
- A set of `subscribedStoreAction` it responds to

### 3. Composers
Composers orchestrate multiple Stores, combining their states and routing actions appropriately.

### 4. WidgetIds
Widget identifiers that link UI components to their corresponding Stores.

---

## File Documentation

### Action Package

#### `Action.kt`
```kotlin
interface Action {
    val actionId: ActionId
}
```
**Purpose**: Base interface for all actions in the system. Every action must have a unique identifier.

**Usage Example**:
```kotlin
// Define an action
data class MyCustomAction(
    val data: String,
    override val actionId: ActionId = MyCustomActionId
) : StoreAction

// Define the action ID
object MyCustomActionId : ActionId {
    override val id: String = "my_custom_action"
}
```

---

#### `ActionId.kt`
```kotlin
interface ActionId {
    val id: String
}
```
**Purpose**: Identifies actions uniquely. Used by Stores to filter which actions they respond to.

**Usage Example**:
```kotlin
object RefreshDataActionId : ActionId {
    override val id: String = "refresh_data"
}
```

---

#### `ComposerAction.kt`
```kotlin
interface ComposerAction: Action
```
**Purpose**: Marker interface for actions that are processed by composers (not stores). This is the parent of both `DataComposerAction` and `UIComposerAction`.

---

#### `DataComposerAction.kt`
```kotlin
interface DataComposerAction: ComposerAction
```
**Purpose**: Actions that are handled by the DataComposer layer. Used for cross-widget communication or data-layer operations.

**Usage Example**:
```kotlin
data class UpdateAllWidgetsAction(
    val newData: GlobalData,
    override val actionId: ActionId = UpdateAllWidgetsActionId
) : DataComposerAction
```

---

#### `StoreAction.kt`
```kotlin
interface StoreAction: Action
```
**Purpose**: Actions that are dispatched to and handled by Stores. The most common action type.

**Usage Example**:
```kotlin
data class ButtonClickedAction(
    val buttonId: String,
    override val actionId: ActionId = ButtonClickedActionId
) : StoreAction
```

---

#### `UIComposerAction.kt`
```kotlin
interface UIComposerAction: ComposerAction
```
**Purpose**: Actions that bubble up to the UI layer. Used for navigation, showing dialogs, toasts, etc.

**Usage Example**:
```kotlin
data class ShowToastAction(
    val message: String,
    override val actionId: ActionId = ShowToastActionId
) : UIComposerAction
```

---

### Action Holder Package

#### `ActionHolder.kt`
```kotlin
interface ActionHolder {
    val action: Action
    val storeId: StoreId
}
```
**Purpose**: Wrapper that pairs an action with the store that dispatched it. Useful for tracking the origin of actions.

---

#### `DataComposerActionHolder.kt`
```kotlin
data class DataComposerActionHolder(
    override val action: DataComposerAction,
    override val storeId: StoreId = StoreId.Empty
): ActionHolder
```
**Purpose**: Holds DataComposerActions along with the originating store ID.

---

#### `StoreActionHolder.kt`
```kotlin
data class StoreActionHolder(
    override val action: StoreAction,
    override val storeId: StoreId
): ActionHolder
```
**Purpose**: Holds StoreActions with their originating store.

---

#### `UIComposerActionHolder.kt`
```kotlin
data class UIComposerActionHolder(
    override val action: UIComposerAction,
    override val storeId: StoreId
): ActionHolder
```
**Purpose**: Wraps UI actions with store context. Used by UI layer to handle side effects.

**Usage Example**:
```kotlin
// In Fragment
fun handleUIAction(actionHolder: UIComposerActionHolder) {
    when (val action = actionHolder.action) {
        is ShowToastAction -> showToast(action.message)
        is NavigateAction -> navigateTo(action.destination)
    }
}
```

---

### State Package

#### `UIState.kt`
```kotlin
interface UIState {
    val type: UIStateType
    val visible: Boolean
    val widgetId: WidgetId
}
```
**Purpose**: Base interface for all UI states. Every widget state must implement this.

**Properties**:
- `type`: Categorizes the state (default, header, footer)
- `visible`: Controls widget visibility
- `widgetId`: Links state to its widget

**Usage Example**:
```kotlin
data class HeaderWidgetState(
    override val type: UIStateType = UIStateDefaultType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId = HeaderWidgetId,
    val title: String = "",
    val subtitle: String = "",
    val showBackButton: Boolean = true
) : UIState
```

---

#### `UIStateType.kt`
```kotlin
interface UIStateType

data object UIStateDefaultType: UIStateType
```
**Purpose**: Marker interface for categorizing states. The default implementation is `UIStateDefaultType`.

---

#### `HeaderUIStateType.kt`
```kotlin
interface HeaderUIStateType: UIStateType
```
**Purpose**: Marker for header states. When using `ListWithHeaderAndFooterDataComposer`, states with this type are automatically separated into the header stream.

---

#### `FooterUIStateType.kt`
```kotlin
interface FooterUIStateType: UIStateType
```
**Purpose**: Marker for footer states. States with this type go to the footer stream.

---

#### `NoOpsUIState.kt`
```kotlin
data class NoOpsUIState(
    override val type: UIStateType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId
): UIState
```
**Purpose**: A simple UIState implementation for widgets that don't need a store (static content).

---

### Store Package

#### `Store.kt`
```kotlin
abstract class Store<UISTATE: UIState, INITMODEL: StoreInitObj, STOREMODEL: StoreInitObj> {
    abstract val storeId: StoreId
    abstract val subscribedStoreAction: Set<ActionId>
    
    abstract fun initialise(globalModel: INITMODEL)
    abstract suspend fun receive(action: StoreAction, storeId: StoreId)
    open fun invokeOnStateUpdate(prevState: UISTATE?, currentState: UISTATE?) {}
    open fun clear() {}
    open fun reset() {}
}
```
**Purpose**: The core business unit that manages state for a single widget.

**Key Concepts**:
- `storeId`: Unique identifier for this store
- `subscribedStoreAction`: Set of action IDs this store responds to
- `initialise()`: Called to set up initial state
- `receive()`: Called when an action is dispatched to this store

**Usage Example**:
```kotlin
class InputWidgetStore @Inject constructor() : Store<InputState, InitModel, WidgetModel>() {

    override val storeId: StoreId = InputStoreId
    
    override val subscribedStoreAction: Set<ActionId> = setOf(
        InputIncrementActionId,
        InputDecrementActionId,
        InputChangedActionId
    )

    override fun initialise(globalModel: InitModel) {
        emitState {
            InputState(
                input = globalModel.defaultInput,
                minInput = 1,
                maxInput = globalModel.maxAllowed
            )
        }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is InputIncrementAction -> {
                updateState {
                    copy(input = (input + 1).coerceAtMost(maxinput))
                }
            }
            is InputDecrementAction -> {
                updateState {
                    copy(input = (input - 1).coerceAtLeast(minQinput))
                }
            }
            is InputInputChangedAction -> {
                updateState {
                    copy(input = action.newValue.coerceIn(mininput, maxinput))
                }
            }
        }
    }
}
```

---

#### `StoreId.kt`
```kotlin
interface StoreId {
    val id: String
    companion object {
        val Empty = object : StoreId {
            override val id: String = ""
        }
    }
}
```
**Purpose**: Uniquely identifies a store. Used for targeted action dispatch.

**Usage Example**:
```kotlin
object HeaderStoreId : StoreId {
    override val id: String = "header_store"
}

object PriceStoreId : StoreId {
    override val id: String = "price_store"
}
```

---

#### `StoreInitObj.kt`
```kotlin
interface StoreInitObj
```
**Purpose**: Marker interface for initialization data passed to stores.

**Usage Example**:
```kotlin
data class InitModel(
    val symbol: String,
    val transactionType: TransactionType,
    val defaultInput: Int
) : StoreInitObj
```

---

#### `StoreWidgetModel.kt`
```kotlin
interface StoreWidgetModel {
    val widgetId: String
}
```
**Purpose**: Interface for models that can be identified by a widget ID.

---

#### `StoreFactory.kt`
```kotlin
interface StoreFactory<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj> {
    fun get(widgetId: WidgetId): Store<UISTATE, INITOBJ, STOREMODEL>
}
```
**Purpose**: Factory pattern for creating stores based on widget IDs.

**Usage Example**:
```kotlin
class StoreFactoryImpl @Inject constructor(
    private val headerStore: HeaderWidgetStore,
    private val inputStore: InputWidgetStore,
) : StoreFactory<State, InitModel, WidgetModel> {

    private val storeMap: Map<WidgetId, Store<State, InitModel, WidgetModel>> = mapOf(
        HeaderWidgetId to headerStore,
        inputWidgetId to inputStore,
    )

    override fun get(widgetId: WidgetId): Store<State, InitModel, WidgetModel> {
        return storeMap[widgetId] 
            ?: throw IllegalArgumentException("Unknown widget: $widgetId")
    }
}
```

---

#### `StoreSyntax.kt`
Extension functions for Store operations:

```kotlin
// Update state with transformation
fun Store<...>.updateState(block: UISTATE.() -> UISTATE?): UISTATE?

// Get current state
val Store<...>.currentState: UISTATE?

// Emit a new state
fun Store<...>.emitState(block: () -> UISTATE)

// Get the coroutine scope
val Store<...>.storeScope: CoroutineScope

// Dispatch action (suspending)
suspend fun Store<...>.suspendDispatch(action: ComposerAction)

// Dispatch action (non-suspending)
fun Store<...>.dispatch(action: ComposerAction): Boolean

// Send action to this store
suspend fun Store<...>.send(action: StoreAction)
suspend fun Store<...>.send(action: StoreAction, storeId: StoreId)
```

**Usage Example**:
```kotlin
class MyStore : Store<MyState, InitModel, WidgetModel>() {
    
    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is UpdateDataAction -> {
                // Update state using the copy pattern
                updateState {
                    copy(data = action.newData, isLoading = false)
                }
            }
            is LoadMoreAction -> {
                // Access current state
                val current = currentState ?: return
                
                // Launch coroutine in store scope
                storeScope.launch {
                    val moreData = repository.loadMore(current.page + 1)
                    updateState {
                        copy(items = items + moreData, page = page + 1)
                    }
                }
            }
            is NotifyParentAction -> {
                // Dispatch to parent composer
                suspendDispatch(DataLoadedComposerAction(currentState?.items.orEmpty()))
            }
        }
    }
}
```

---

### Composer Package

#### `Composer.kt`
```kotlin
interface Composer
```
**Purpose**: Base marker interface for all composers.

---

#### `DataComposer.kt`
```kotlin
interface DataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>: Composer {
    val uiStateFlow: StateFlow<List<UISTATE>>
    val uiActionHolder: SharedFlow<UIComposerActionHolder>
    
    suspend fun initialiseWithWidgets(widgets: List<WidgetId>, initObj: INITOBJ)
    suspend fun updateWidgets(widgets: List<WidgetId>, initobj: INITOBJ)
    fun initialiseWithInitModel(initObj: INITOBJ)
    
    suspend fun suspendDispatch(action: Action)
    suspend fun suspendDispatchToStore(action: StoreAction, storeId: StoreId)
    suspend fun suspendDispatchToWidget(action: StoreAction, widgetId: WidgetId)
    suspend fun suspendBatchDispatchToWidget(storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>)
    
    fun currentWidgetIds(): List<WidgetId>
    fun dispose()
    
    fun dispatch(action: Action)
    fun dispatchToStore(action: StoreAction, storeId: StoreId)
    fun dispatchToWidget(action: StoreAction, widgetId: WidgetId)
}
```
**Purpose**: Core interface for data management. Manages multiple stores and combines their states.

---

#### `SingleDataComposer.kt`
```kotlin
interface SingleDataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>
    : DataComposer<UISTATE, INITOBJ, STOREMODEL>
```
**Purpose**: For screens with a single widget/store.

---

#### `ListDataComposer.kt`
```kotlin
interface ListDataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>
    : DataComposer<UISTATE, INITOBJ, STOREMODEL>
```
**Purpose**: For screens with a list of widgets/stores.

---

#### `ListWithHeaderAndFooterDataComposer.kt`
```kotlin
interface ListWithHeaderAndFooterDataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>
    : ListDataComposer<UISTATE, INITOBJ, STOREMODEL> {
    val headerState: StateFlow<List<UISTATE>>
    val footerState: StateFlow<List<UISTATE>>
}
```
**Purpose**: Extended list composer with separate header and footer state streams.

---

#### `DataComposerActionHandler.kt`
```kotlin
interface DataComposerActionHandler {
    suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder)
    fun receiveAllActions(action: Action)
}
```
**Purpose**: Callback interface for handling DataComposerActions. Usually implemented by the ViewModel.

**Usage Example**:
```kotlin
class ViewModel : ListDataComposerViewModel<...>(...), DataComposerActionHandler {
    
    override val dataComposerActionHandler: DataComposerActionHandler = this
    
    override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) {
        when (val action = dataComposerActionHolder.action) {
            is RefreshAllWidgetsAction -> {
                // Re-initialize all stores
                container.initialiseWithInitModel(currentInitModel)
            }
            is NavigateToDetailsAction -> {
                _navigationEvent.emit(NavigationTarget.Details(action.itemId))
            }
        }
    }
    
    override fun receiveAllActions(action: Action) {
        // Called for every action that flows through the system
        // Useful for logging/analytics
        analytics.trackAction(action)
    }
}
```

---

### Composer Host Interfaces

#### `DataComposerHost.kt`
```kotlin
interface DataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> {
    val container: DataComposer<UISTATE, INITDATA, STOREMODEL>
}
```
**Purpose**: Interface for classes that host a DataComposer (typically ViewModels).

---

#### `SingleDataComposerHost.kt`
```kotlin
interface SingleDataComposerHost<...>: DataComposerHost<...> {
    override val container: SingleDataComposer<UISTATE, INITDATA, STOREMODEL>
}
```

---

#### `ListDataComposerHost.kt`
```kotlin
interface ListDataComposerHost<...>: DataComposerHost<...> {
    override val container: ListDataComposer<UISTATE, INITDATA, STOREMODEL>
}
```

---

#### `ListWithHeaderAndFooterDataComposerHost.kt`
```kotlin
interface ListWithHeaderAndFooterDataComposerHost<...>: ListDataComposerHost<...> {
    override val container: ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA, STOREMODEL>
}
```

---

### Composer Syntax Extensions

#### `DataComposerHostSyntax.kt`
Provides convenient extension functions for DataComposerHost:

```kotlin
// Dispatch actions
fun DataComposerHost<...>.dispatch(action: Action)
suspend fun DataComposerHost<...>.suspendDispatch(action: Action)

// Dispatch to specific store/widget
suspend fun DataComposerHost<...>.suspendDispatch(action: StoreAction, storeId: StoreId)
suspend fun DataComposerHost<...>.suspendDispatch(action: StoreAction, widgetId: WidgetId)
suspend fun DataComposerHost<...>.suspendBatchDispatch(storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>)

// Non-suspending variants
fun DataComposerHost<...>.dispatch(action: StoreAction, storeId: StoreId)
fun DataComposerHost<...>.dispatch(action: StoreAction, widgetId: WidgetId)

// Initialize
suspend fun DataComposerHost<...>.init(widgets: List<WidgetId>, initData: INITDATA)
suspend fun DataComposerHost<...>.updateWidget(widgets: List<WidgetId>, initData: INITDATA)

// State access
val DataComposerHost<...>.uiState: StateFlow<List<UISTATE>>
val DataComposerHost<...>.uiActionHolder: SharedFlow<UIComposerActionHolder>
val DataComposerHost<...>.currentWidgetIds: List<WidgetId>

// Observers
fun DataComposerHost<...>.observeAsState(coroutineScope: CoroutineScope, observer: List<UISTATE>.() -> Unit)
fun DataComposerHost<...>.observeActions(coroutineScope: CoroutineScope, observer: UIComposerActionHolder.() -> Unit)
```

---

#### `SingleDataComposerHostSyntax.kt`
```kotlin
fun SingleDataComposerHost<...>.observeState(
    coroutineScope: CoroutineScope,
    observer: UISTATE.() -> Unit
)
```
**Purpose**: Convenience function that extracts the first (and only) state from the list.

---

#### `ListWithHeaderAndFooterDataComposerHostSyntax.kt`
```kotlin
fun ListWithHeaderAndFooterDataComposerHost<...>.observeHeaderState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
)

fun ListWithHeaderAndFooterDataComposerHost<...>.observeFooterState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
)

val ListWithHeaderAndFooterDataComposerHost<...>.headerState: StateFlow<List<UISTATE>>
val ListWithHeaderAndFooterDataComposerHost<...>.footerState: StateFlow<List<UISTATE>>
```

---

### UI Composer Package

#### `UIComposer.kt`
```kotlin
interface UIComposer<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>: Composer {
    val container: DataComposerHost<UISTATE, INITDATA, STOREMODEL>
}
```
**Purpose**: UI-layer composer that wraps a DataComposerHost.

---

#### `ListUIComposer.kt`
```kotlin
interface ListUIComposer<...>: UIComposer<...> {
    override val container: ListDataComposerHost<UISTATE, INITDATA, STOREMODEL>
}
```

---

#### `SingleUIComposer.kt`
```kotlin
interface SingleUIComposer<...>: UIComposer<...> {
    override val container: SingleDataComposerHost<UISTATE, INITDATA, STOREMODEL>
}
```

---

#### `ListUIComposerWithHeaderAndFooter.kt`
```kotlin
interface ListUIComposerWithHeaderAndFooter<...>: UIComposer<...> {
    override val container: ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA, STOREMODEL>
}
```

---

### Widget ID Classes

#### `WidgetId.kt`
```kotlin
interface WidgetId {
    val id: String
    companion object {
        val Empty = object : StoreId {
            override val id: String = ""
        }
    }
}
```
**Purpose**: Base interface for widget identification.

---

#### `HostWidgetId.kt`
```kotlin
interface HostWidgetId: WidgetId
```
**Purpose**: Marker for widgets that can host child widgets (used with `GroupWidget`).

---

#### `ChildWidgetId.kt`
```kotlin
interface ChildWidgetId: WidgetId {
    val uiState: UIState
}
```
**Purpose**: For child widgets within a group that have static state.

---

#### `NoStoreWidgetId.kt`
```kotlin
interface NoStoreWidgetId: WidgetId {
    val uiState: UIState
}
```
**Purpose**: For widgets that don't need a store (static content).

---

#### `GroupWidget.kt`
```kotlin
interface GroupWidget: WidgetId {
    val hostId: HostWidgetId
    val topChildren: List<ChildWidgetId>
    val bottomChildren: List<ChildWidgetId>
}
```
**Purpose**: Groups a host widget with its child widgets. When the host is hidden, all children are also hidden.

---

#### `StoreActionWidgetIdPair.kt`
```kotlin
data class StoreActionWidgetIdPair(
    val action: StoreAction,
    val widgetId: WidgetId
)
```
**Purpose**: Pairs an action with a target widget for batch dispatching.

---

### UI Composer Syntax

#### `UIComposerSyntax.kt`
```kotlin
fun UIComposer<...>.dispatch(action: StoreAction)
fun UIComposer<...>.dispatch(action: StoreAction, storeId: StoreId)
fun UIComposer<...>.dispatch(action: DataComposerAction)

fun UIComposer<...>.observeAsState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
)

fun UIComposer<...>.observeAction(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: UIComposerActionHolder.() -> Unit
)
```
**Purpose**: Lifecycle-aware observation functions for Fragments/Activities.

---

#### `ListUIComposerWithHeaderAndFooterSyntax.kt`
```kotlin
fun ListUIComposerWithHeaderAndFooter<...>.observeHeaderState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
)

fun ListUIComposerWithHeaderAndFooter<...>.observeFooterState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
)
```

---

### Extensions

#### `HostExtensions.kt`
Factory functions for creating composers:

```kotlin
fun <...> listDataComposer(
    storeFactory: StoreFactory<...>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): ListDataComposer<...>

fun <...> singleDataComposer(
    storeFactory: StoreFactory<...>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): SingleDataComposer<...>

fun <...> listWithHeaderAndFooterDataComposer(
    storeFactory: StoreFactory<...>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): ListWithHeaderAndFooterDataComposer<...>
```

---

#### `CoroutineExtensions.kt`
```kotlin
internal fun <T: ActionHolder, ...> CoroutineScope.collectActionHolder(
    list: List<Store<...>>,
    property: Store<...>.() -> SharedFlow<ActionHolder>,
    block: suspend (T) -> Unit
): Job
```
**Purpose**: Internal utility for collecting action holders from multiple stores.

---

### UI Components

#### `ListUIComposerFragment.kt`
```kotlin
abstract class ListUIComposerFragment<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    @LayoutRes private val layoutId: Int
): Fragment(layoutId), ListUIComposer<UISTATE, INITDATA, STOREMODEL> {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAsState(lifecycleOwner = viewLifecycleOwner, observer = ::render)
        observeAction(lifecycleOwner = viewLifecycleOwner, observer = ::handleUIAction)
    }
    
    abstract fun render(state: List<UISTATE>)
    abstract fun handleUIAction(actionHolder: UIComposerActionHolder)
}
```
**Purpose**: Base Fragment for list-based UI composition.

---

#### `ListUIComposerWithHeaderAndFooterFragment.kt`
```kotlin
abstract class ListUIComposerWithHeaderAndFooterFragment<...>(
    @LayoutRes private val layoutId: Int
): ListUIComposerFragment<...>(layoutId), ListUIComposerWithHeaderAndFooter<...> {

    abstract override val container: ListWithHeaderAndFooterDataComposerHost<...>
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHeaderState(lifecycleOwner = viewLifecycleOwner, observer = ::renderHeader)
        observeFooterState(lifecycleOwner = viewLifecycleOwner, observer = ::renderFooter)
    }

    abstract fun renderHeader(list: List<UISTATE>)
    abstract fun renderFooter(list: List<UISTATE>)
}
```
**Purpose**: Base Fragment with header/footer support.

---

### ViewModels

#### `ListDataComposerViewModel.kt`
```kotlin
abstract class ListDataComposerViewModel<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>
) : ViewModel(), ListDataComposerHost<UISTATE, INITDATA, STOREMODEL> {
    
    abstract val dataComposerActionHandler: DataComposerActionHandler
    
    override val container: ListDataComposer<...> by lazy {
        listDataComposer(
            storeFactory = storeFactory,
            coroutineScope = viewModelScope,
            dataComposerActionHandler = dataComposerActionHandler
        )
    }
}
```
**Purpose**: Base ViewModel for list-based screens.

---

#### `ListWithHeaderAndFooterDataComposerViewModel.kt`
```kotlin
abstract class ListWithHeaderAndFooterDataComposerViewModel<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    storeFactory: StoreFactory<...>
) : ViewModel(), ListWithHeaderAndFooterDataComposerHost<...> {
    
    protected abstract val dataComposerActionHandler: DataComposerActionHandler
    
    override val container: ListWithHeaderAndFooterDataComposer<...> by lazy {
        listWithHeaderAndFooterDataComposer(
            storeFactory = storeFactory,
            coroutineScope = viewModelScope,
            dataComposerActionHandler = dataComposerActionHandler
        )
    }
}
```
**Purpose**: Base ViewModel with header/footer support.

---

---

## Best Practices

### 1. Store Design
- Keep stores focused on a single widget's concerns
- Define clear `subscribedStoreAction` sets
- Use `reset()` for subscriptions that should persist across re-initialization

### 2. Action Design
- Create specific actions rather than generic ones
- Include all necessary data in the action
- Use sealed interfaces for related actions

### 3. State Design
- Make states immutable (data classes)
- Include `widgetId` for identification
- Use `visible` flag for conditional rendering

### 4. Dispatching
- Use `suspendDispatch` in suspend contexts
- Use `dispatch` for fire-and-forget scenarios
- Use `dispatchToWidget` for targeted updates

### 5. Testing
- Test stores in isolation by calling `receive()` directly
- Mock the `StoreFactory` for ViewModel tests
- Use `runTest` for coroutine testing

---

## Dependencies

```kotlin
dependencies {
    implementation(libs.bundles.lifecycle)  // ViewModel, lifecycleScope
    implementation(libs.bundles.coroutine)  // Flow, coroutines
}
```

---

## Migration Guide

### From Traditional ViewModel to StoreComposer

1. Identify widgets on your screen
2. Create `UIState` for each widget
3. Create `Store` for each widget
4. Create `StoreFactory`
5. Extend `ListDataComposerViewModel` or `ListWithHeaderAndFooterDataComposerViewModel`
6. Implement `DataComposerActionHandler`
7. Update Fragment to implement `ListUIComposer` interface

---

*Generated documentation for the StoreComposer module v1.0*

