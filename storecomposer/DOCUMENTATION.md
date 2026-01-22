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
class QuantityWidgetStore @Inject constructor(
    private val formSession: OrderFormSession
) : Store<QuantityState, OrderInitModel, OrderWidgetModel>() {

    override val storeId: StoreId = QuantityStoreId
    
    override val subscribedStoreAction: Set<ActionId> = setOf(
        QuantityIncrementActionId,
        QuantityDecrementActionId,
        QuantityInputChangedActionId
    )

    override fun initialise(globalModel: OrderInitModel) {
        emitState {
            QuantityState(
                quantity = globalModel.defaultQuantity,
                minQuantity = 1,
                maxQuantity = globalModel.maxAllowed
            )
        }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is QuantityIncrementAction -> {
                updateState {
                    copy(quantity = (quantity + 1).coerceAtMost(maxQuantity))
                }
            }
            is QuantityDecrementAction -> {
                updateState {
                    copy(quantity = (quantity - 1).coerceAtLeast(minQuantity))
                }
            }
            is QuantityInputChangedAction -> {
                updateState {
                    copy(quantity = action.newValue.coerceIn(minQuantity, maxQuantity))
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
data class OrderPadInitModel(
    val symbol: String,
    val transactionType: TransactionType,
    val defaultQuantity: Int
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
class OrderPadStoreFactoryImpl @Inject constructor(
    private val headerStore: HeaderWidgetStore,
    private val quantityStore: QuantityWidgetStore,
    private val priceStore: PriceWidgetStore
) : StoreFactory<OrderPadState, OrderPadInitModel, OrderWidgetModel> {

    private val storeMap: Map<WidgetId, Store<OrderPadState, OrderPadInitModel, OrderWidgetModel>> = mapOf(
        HeaderWidgetId to headerStore,
        QuantityWidgetId to quantityStore,
        PriceWidgetId to priceStore
    )

    override fun get(widgetId: WidgetId): Store<OrderPadState, OrderPadInitModel, OrderWidgetModel> {
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
class OrderPadViewModel : ListDataComposerViewModel<...>(...), DataComposerActionHandler {
    
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

## Usage Examples

### Example 1: Complete Order Pad Implementation

This example shows how the OrderPad feature uses StoreComposer:

#### 1. Define State
```kotlin
// Base state interface
sealed interface OrderPadState : UIState

// Specific widget state
data class QuantityWidgetState(
    override val type: UIStateType = UIStateDefaultType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId = QuantityWidgetId,
    val quantity: Int = 1,
    val minQuantity: Int = 1,
    val maxQuantity: Int = 999,
    val lotSize: Int = 1
) : OrderPadState

data class PriceWidgetState(
    override val type: UIStateType = UIStateDefaultType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId = PriceWidgetId,
    val price: String = "",
    val priceType: PriceType = PriceType.MARKET,
    val isEditable: Boolean = true
) : OrderPadState
```

#### 2. Define Widget IDs
```kotlin
sealed interface OrderPadWidgetId : WidgetId

object QuantityWidgetId : OrderPadWidgetId {
    override val id: String = "quantity_widget"
}

object PriceWidgetId : OrderPadWidgetId {
    override val id: String = "price_widget"
}

object HeaderWidgetId : OrderPadWidgetId, HostWidgetId {
    override val id: String = "header_widget"
}
```

#### 3. Define Store IDs
```kotlin
sealed interface OrderPadStoreId : StoreId

object QuantityStoreId : OrderPadStoreId {
    override val id: String = "quantity_store"
}

object PriceStoreId : OrderPadStoreId {
    override val id: String = "price_store"
}
```

#### 4. Define Actions
```kotlin
// Action IDs
object QuantityChangedActionId : ActionId {
    override val id: String = "quantity_changed"
}

object PriceChangedActionId : ActionId {
    override val id: String = "price_changed"
}

// Actions
data class QuantityChangedAction(
    val newQuantity: Int,
    override val actionId: ActionId = QuantityChangedActionId
) : StoreAction

data class PriceChangedAction(
    val newPrice: String,
    override val actionId: ActionId = PriceChangedActionId
) : StoreAction

// UI Actions
data class ShowKeyboardAction(
    val targetField: String,
    override val actionId: ActionId = ShowKeyboardActionId
) : UIComposerAction
```

#### 5. Implement Stores
```kotlin
class QuantityWidgetStoreImpl @Inject constructor(
    private val formSession: OrderPadFormSession
) : Store<QuantityWidgetState, OrderPadInitModel, OrderWidgetModel>() {

    override val storeId: StoreId = QuantityStoreId

    override val subscribedStoreAction: Set<ActionId> = setOf(
        QuantityChangedActionId,
        QuantityIncrementActionId,
        QuantityDecrementActionId
    )

    override fun initialise(globalModel: OrderPadInitModel) {
        emitState {
            QuantityWidgetState(
                quantity = globalModel.defaultQuantity,
                lotSize = globalModel.lotSize,
                maxQuantity = globalModel.maxQuantity
            )
        }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is QuantityChangedAction -> {
                updateState {
                    val validQuantity = action.newQuantity.coerceIn(minQuantity, maxQuantity)
                    copy(quantity = validQuantity)
                }
                // Update form session
                formSession.updateField(OrderFormField.QUANTITY, currentState?.quantity)
            }
            is QuantityIncrementAction -> {
                updateState {
                    copy(quantity = (quantity + lotSize).coerceAtMost(maxQuantity))
                }
            }
        }
    }
    
    override fun reset() {
        // Subscribe to external updates
        formSession.registerForField(OrderFormField.QUANTITY) { _, newValue ->
            updateState {
                copy(quantity = newValue as? Int ?: quantity)
            }
        }
    }
}
```

#### 6. Implement Store Factory
```kotlin
class OrderPadStoreFactoryImpl @Inject constructor(
    private val quantityStore: Provider<QuantityWidgetStoreImpl>,
    private val priceStore: Provider<PriceWidgetStoreImpl>,
    private val headerStore: Provider<HeaderWidgetStoreImpl>
) : StoreFactory<OrderPadState, OrderPadInitModel, OrderWidgetModel> {

    override fun get(widgetId: WidgetId): Store<OrderPadState, OrderPadInitModel, OrderWidgetModel> {
        return when (widgetId) {
            QuantityWidgetId -> quantityStore.get()
            PriceWidgetId -> priceStore.get()
            HeaderWidgetId -> headerStore.get()
            else -> throw IllegalArgumentException("Unknown widget: $widgetId")
        } as Store<OrderPadState, OrderPadInitModel, OrderWidgetModel>
    }
}
```

#### 7. Implement ViewModel
```kotlin
@HiltViewModel
class OrderPadViewModel @Inject constructor(
    storeFactory: OrderPadStoreFactory,
    private val formSession: OrderPadFormSession
) : ListWithHeaderAndFooterDataComposerViewModel<OrderPadState, OrderPadInitModel, OrderWidgetModel>(
    storeFactory = storeFactory
), DataComposerActionHandler {

    override val dataComposerActionHandler: DataComposerActionHandler = this

    private val _navigationEvent = MutableSharedFlow<NavigationTarget>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun initialize(orderData: OrderData) {
        viewModelScope.launch {
            val initModel = OrderPadInitModel(
                symbol = orderData.symbol,
                defaultQuantity = orderData.quantity,
                lotSize = orderData.lotSize
            )
            
            val widgets = listOf(
                HeaderWidgetId,
                QuantityWidgetId,
                PriceWidgetId
            )
            
            init(widgets = widgets, initData = initModel)
        }
    }

    override suspend fun receiveAction(holder: DataComposerActionHolder) {
        when (val action = holder.action) {
            is CloseOrderPadAction -> {
                _navigationEvent.emit(NavigationTarget.Back)
            }
            is PlaceOrderAction -> {
                placeOrder()
            }
        }
    }

    override fun receiveAllActions(action: Action) {
        // Log all actions for debugging
        Timber.d("Action: ${action.actionId.id}")
    }
    
    fun onQuantityChanged(quantity: Int) {
        dispatch(QuantityChangedAction(quantity))
    }
}
```

#### 8. Implement Fragment
```kotlin
@AndroidEntryPoint
class OrderPadFragment : ListUIComposerWithHeaderAndFooterFragment<
    OrderPadState, 
    OrderPadInitModel, 
    OrderWidgetModel
>(R.layout.fragment_order_pad), ListUIComposerWithHeaderAndFooter<OrderPadState, OrderPadInitModel, OrderWidgetModel> {

    private val viewModel: OrderPadViewModel by viewModels()
    private lateinit var binding: FragmentOrderPadBinding

    override val container: ListWithHeaderAndFooterDataComposerHost<OrderPadState, OrderPadInitModel, OrderWidgetModel>
        get() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderPadBinding.bind(view)
        
        // Initialize with data from arguments
        viewModel.initialize(args.orderData)
        
        // Observe navigation events
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigationEvent.collect { target ->
                when (target) {
                    NavigationTarget.Back -> findNavController().popBackStack()
                }
            }
        }
    }

    override fun render(state: List<OrderPadState>) {
        // Update RecyclerView adapter
        adapter.submitList(state)
    }

    override fun renderHeader(list: List<OrderPadState>) {
        list.filterIsInstance<HeaderWidgetState>().firstOrNull()?.let { state ->
            binding.headerView.render(state)
        }
    }

    override fun renderFooter(list: List<OrderPadState>) {
        list.filterIsInstance<FooterWidgetState>().firstOrNull()?.let { state ->
            binding.footerView.render(state)
        }
    }

    override fun handleUIAction(actionHolder: UIComposerActionHolder) {
        when (val action = actionHolder.action) {
            is ShowKeyboardAction -> {
                showKeyboard(action.targetField)
            }
            is ShowBottomSheetAction -> {
                showBottomSheet(action.sheetData)
            }
            is ShowToastAction -> {
                showToast(action.message)
            }
        }
    }
}
```

---

### Example 2: Dispatching Actions

```kotlin
// From ViewModel
class MyViewModel : ListDataComposerViewModel<...>(...) {
    
    // Dispatch to all stores that subscribe to this action
    fun refreshAll() {
        dispatch(RefreshDataAction())
    }
    
    // Dispatch to specific store by ID
    fun updateHeader(title: String) {
        dispatch(UpdateTitleAction(title), HeaderStoreId)
    }
    
    // Dispatch to specific widget
    fun updateQuantity(widgetId: WidgetId, quantity: Int) {
        dispatch(QuantityChangedAction(quantity), widgetId)
    }
    
    // Batch dispatch (suspending)
    suspend fun updateMultiple(updates: List<Pair<WidgetId, Int>>) {
        val pairs = updates.map { (widgetId, value) ->
            StoreActionWidgetIdPair(
                action = UpdateValueAction(value),
                widgetId = widgetId
            )
        }
        suspendBatchDispatch(pairs)
    }
}

// From Store - dispatch to parent
class MyStore : Store<...>() {
    
    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is ItemClickedAction -> {
                // Dispatch UI action (will bubble to Fragment)
                suspendDispatch(NavigateToDetailsUIAction(action.itemId))
            }
            is DataLoadedAction -> {
                // Dispatch to DataComposer (will be handled by ViewModel)
                suspendDispatch(NotifyDataLoadedComposerAction())
            }
        }
    }
}
```

---

### Example 3: Using GroupWidget for Dependent Visibility

```kotlin
// Define a group where child widgets depend on parent visibility
data class SectionGroupWidget(
    override val hostId: HostWidgetId = SectionHeaderWidgetId,
    override val topChildren: List<ChildWidgetId> = emptyList(),
    override val bottomChildren: List<ChildWidgetId> = listOf(
        DividerChildWidgetId,
        SpacerChildWidgetId
    ),
    override val id: String = "section_group"
) : GroupWidget

// When initializing widgets
val widgets = listOf(
    SectionGroupWidget(
        hostId = AdvancedOptionsHeaderId,
        bottomChildren = listOf(
            AdvancedOption1ChildId,
            AdvancedOption2ChildId
        )
    ),
    PriceWidgetId,
    QuantityWidgetId
)

container.init(widgets, initModel)

// When AdvancedOptionsHeader is hidden, all bottomChildren are also hidden
```

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

