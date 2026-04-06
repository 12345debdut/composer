---
title: Getting Started
nav_order: 2
---

# Getting Started

## Installation

### Using the BOM (recommended)

The BOM manages versions for all Composer artifacts automatically:

```kotlin
// build.gradle.kts
dependencies {
    implementation(platform("io.github.debdutsaha:composer-bom:1.0.0"))
    implementation("io.github.debdutsaha:composer")
    implementation("io.github.debdutsaha:composer-compose") // optional: Jetpack Compose extensions
}
```

### Individual artifacts

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.debdutsaha:composer:1.0.0")
    implementation("io.github.debdutsaha:composer-compose:1.0.0") // optional
}
```

```groovy
// build.gradle (Groovy)
dependencies {
    implementation 'io.github.debdutsaha:composer:1.0.0'
    implementation 'io.github.debdutsaha:composer-compose:1.0.0' // optional
}
```

The library is on **Maven Central** -- no extra repository configuration needed.

---

## Quick Start

### 1. Define your state

```kotlin
import com.debdut.composer.state.UIState
import com.debdut.composer.state.UIStateType
import com.debdut.composer.state.UIStateDefaultType
import com.debdut.composer.composer.ui.WidgetId

object CounterWidgetId : WidgetId {
    override val id: String = "counter_widget"
}

data class CounterState(
    override val widgetId: WidgetId = CounterWidgetId,
    override val visible: Boolean = true,
    override val type: UIStateType = UIStateDefaultType,
    val count: Int = 0
) : UIState
```

### 2. Create actions

```kotlin
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction

object IncrementActionId : ActionId {
    override val id: String = "increment"
}

object DecrementActionId : ActionId {
    override val id: String = "decrement"
}

data class IncrementAction(
    val amount: Int = 1,
    override val actionId: ActionId = IncrementActionId
) : StoreAction

data class DecrementAction(
    val amount: Int = 1,
    override val actionId: ActionId = DecrementActionId
) : StoreAction
```

### 3. Create a Store

```kotlin
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.StoreWidgetModel
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction

object CounterStoreId : StoreId {
    override val id: String = "counter_store"
}

data class InitModel : StoreInitObj

data class WidgetModel : StoreWidgetModel {
    override val widgetId: String = CounterWidgetId.id
}

class CounterStore : Store<CounterState, InitModel, WidgetModel>() {

    override val storeId: StoreId = CounterStoreId

    override val subscribedStoreAction: Set<ActionId> = setOf(
        IncrementActionId,
        DecrementActionId
    )

    override fun initialise(globalModel: InitModel) {
        emitState {
            CounterState(count = 0)
        }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is IncrementAction -> {
                updateState {
                    copy(count = count + action.amount)
                }
            }
            is DecrementAction -> {
                updateState {
                    copy(count = (count - action.amount).coerceAtLeast(0))
                }
            }
        }
    }
}
```

### 4. Create a Store Factory

```kotlin
import com.debdut.composer.store.factory.StoreFactory

class CounterStoreFactory : StoreFactory<CounterState, InitModel, WidgetModel> {

    private val counterStore = CounterStore()

    override fun get(widgetId: WidgetId): Store<CounterState, InitModel, WidgetModel> {
        return when (widgetId) {
            CounterWidgetId -> counterStore
            else -> throw IllegalArgumentException("Unknown widget: $widgetId")
        }
    }
}
```

### 5. Create a ViewModel

```kotlin
import com.debdut.composer.viewmodel.ListDataComposerViewModel
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.action.Action

class CounterViewModel(
    storeFactory: StoreFactory<CounterState, InitModel, WidgetModel>
) : ListDataComposerViewModel<CounterState, InitModel, WidgetModel>(storeFactory),
    DataComposerActionHandler {

    override val dataComposerActionHandler: DataComposerActionHandler = this

    override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) {
        // Handle DataComposerActions if needed
    }

    override fun receiveAllActions(action: Action) {
        // Optional: Log or track all actions
    }
}
```

### 6. Use in a Fragment

```kotlin
import com.debdut.composer.composer.ui.ListUIComposer
import com.debdut.composer.composer.ui.syntax.observeAsState
import com.debdut.composer.composer.ui.syntax.observeAction
import com.debdut.composer.composer.ui.syntax.dispatch

class CounterFragment : Fragment(R.layout.fragment_counter),
    ListUIComposer<CounterState, InitModel, WidgetModel> {

    private val viewModel: CounterViewModel by viewModels {
        CounterViewModelFactory(CounterStoreFactory())
    }

    override val container: ListDataComposerHost<CounterState, InitModel, WidgetModel>
        get() = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAsState(viewLifecycleOwner) { states ->
            states.firstOrNull()?.let { state ->
                binding.counterText.text = "Count: ${state.count}"
            }
        }

        observeAction(viewLifecycleOwner) { holder ->
            when (val action = holder.action) {
                is ShowToastAction -> {
                    Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.init(
                widgets = listOf(CounterWidgetId),
                initData = InitModel()
            )
        }

        binding.incrementButton.setOnClickListener {
            dispatch(IncrementAction(amount = 1))
        }

        binding.decrementButton.setOnClickListener {
            dispatch(DecrementAction(amount = 1))
        }
    }
}
```

### 7. Dependency injection (optional)

If using Hilt, Koin, or similar:

```kotlin
@Module
@InstallIn(ViewModelComponent::class)
object CounterModule {

    @Provides
    fun provideCounterStoreFactory(): StoreFactory<CounterState, InitModel, WidgetModel> {
        return CounterStoreFactory()
    }
}
```
