# Composer Library

<div align="center">

**A modern, unidirectional state management library for Android applications**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-24+-green.svg)](https://developer.android.com)
[![Version](https://img.shields.io/badge/Version-1.0.0-orange.svg)](https://github.com/12345debdut/composer)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)

[Overview](#-overview) • [Architecture](#️-architecture) • [Installation](#-installation) • [Quick Start](#-quick-start) • [Documentation](#-documentation) • [License](#-license)

</div>

---

## 🚀 Add to Your Project

### Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.debdut.library:composer:1.0.0")
}
```

```groovy
// build.gradle
dependencies {
    implementation 'com.debdut.library:composer:1.0.0'
}
```

### Repository

Add to `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/12345debdut/composer")
            credentials {
                username = project.findProperty("GITHUB_USER") as String? ?: System.getenv("GITHUB_USER")
                password = project.findProperty("GITHUB_TOKEN") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

> ⚠️ **Note:** You'll need a GitHub Personal Access Token with `read:packages` permission. See [Installation Guide](#-installation) for detailed setup.

---

# Composer Module Documentation

## Table of Contents
1. [Overview](#-overview)
2. [Architecture](#️-architecture)
3. [Installation](#-installation)
4. [Quick Start](#-quick-start)
5. [Core Concepts](#-core-concepts)
6. [File Documentation](#-file-documentation)
7. [Usage Examples](#-usage-examples)
8. [Best Practices](#-best-practices)

---

## 📦 Dependency Information

### Version

**Current Version:** `1.0.0`

### Maven Coordinates

```
Group ID:    com.debdut.library
Artifact ID: composer
Version:     1.0.0
```

### Repository

```
https://maven.pkg.github.com/12345debdut/composer
```

### Requirements

- **Minimum SDK:** 24
- **Kotlin:** 1.9+
- **Gradle:** 8.0+

> 📖 **See [Installation Guide](#-installation) below for complete setup instructions with authentication**

---

## 📖 Overview

**Composer** is a state management library that implements a unidirectional data flow architecture for Android applications. It provides a structured, scalable approach to managing UI state across multiple widgets and components using a Store-based pattern with reactive state updates via Kotlin Flows.

### What is Composer?

Composer solves the challenge of building complex Android screens with multiple interactive widgets by providing:

- **Store-based state management**: Each widget has its own isolated Store that manages its state
- **Composable architecture**: Multiple stores can be composed together seamlessly
- **Action-based communication**: Type-safe actions flow between UI, Stores, and Composers
- **Reactive state updates**: Uses Kotlin Flows for reactive state propagation
- **Unidirectional data flow**: Actions flow down, state flows up (predictable and debuggable)
- **Lifecycle-aware**: Automatic lifecycle management for UI observation
- **Easy testing**: Business logic in Stores is easily testable in isolation

### Key Benefits

- ✅ **Decoupled widget state management** - Each widget's state is independent
- ✅ **Predictable state updates** - Unidirectional flow ensures traceable changes
- ✅ **Scalable architecture** - Perfect for complex screens with multiple widgets
- ✅ **Type-safe actions** - Compile-time safety for all state changes
- ✅ **Reactive by design** - Built on Kotlin Flows for modern reactive programming
- ✅ **Testable** - Stores can be tested independently without UI dependencies

---

## 🏗️ Architecture

Composer follows a unidirectional data flow pattern with three main layers:

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

The unidirectional data flow ensures predictable state management:

1. **User Interaction** → UI dispatches `StoreAction`
2. **Action Routing** → DataComposer routes action to appropriate Store(s)
3. **State Update** → Store processes action and updates its state
4. **State Emission** → New state flows back through DataComposer
5. **UI Update** → UIComposer receives combined state and renders UI

### Key Components

- **Store**: Manages state for a single widget, processes actions
- **DataComposer**: Orchestrates multiple Stores, combines their states
- **UIComposer**: Lifecycle-aware wrapper for UI layer integration
- **Actions**: Immutable data classes describing "what happened"
- **UIState**: Immutable state objects representing widget state

---

## 🚀 Installation

This guide will walk you through adding Composer library to your Android project.

### Prerequisites

- Android Studio (latest version recommended)
- Minimum SDK: 24
- Kotlin 1.9+
- Gradle 8.0+

### Quick Setup

**Dependency:**
```kotlin
implementation("com.debdut.library:composer:1.0.0")
```

**Repository:** `https://maven.pkg.github.com/12345debdut/composer`

### Step 1: Add GitHub Packages Repository

Add the repository to your project's `settings.gradle.kts` (or `settings.gradle`):

**For Kotlin DSL (`settings.gradle.kts`):**

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/12345debdut/composer")
            credentials {
                username = project.findProperty("GITHUB_USER") as String? ?: System.getenv("GITHUB_USER")
                password = project.findProperty("GITHUB_TOKEN") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

**For Groovy DSL (`settings.gradle`):**

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/12345debdut/composer")
            credentials {
                username = project.findProperty("GITHUB_USER") ?: System.getenv("GITHUB_USER")
                password = project.findProperty("GITHUB_TOKEN") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### Step 2: Add Dependency

Add the dependency to your module's `build.gradle.kts` (or `build.gradle`):

**For Kotlin DSL (`build.gradle.kts`):**

```kotlin
dependencies {
    implementation("com.debdut.library:composer:1.0.0")
}
```

**For Groovy DSL (`build.gradle`):**

```groovy
dependencies {
    implementation 'com.debdut.library:composer:1.0.0'
}
```

### Step 3: Configure Authentication

You'll need a GitHub Personal Access Token with `read:packages` permission to access the package.

#### Create a GitHub Personal Access Token

1. Go to [GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens)
2. Click "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Composer Library Access")
4. Select the `read:packages` scope
5. Click "Generate token"
6. **Copy the token immediately** - you won't be able to see it again!

#### Configure Credentials

You have three options for providing credentials:

**Option 1: Using `local.properties` (Recommended for local development)**

Create or edit `local.properties` in your project root:

```properties
GITHUB_USER=your-github-username
GITHUB_TOKEN=ghp_your_personal_access_token_here
```

**Note:** `local.properties` is typically already in `.gitignore`, so your token won't be committed.

**Option 2: Using Environment Variables**

Set environment variables in your system:

**On macOS/Linux:**
```bash
export GITHUB_USER=your-github-username
export GITHUB_TOKEN=ghp_your_personal_access_token_here
```

**On Windows (PowerShell):**
```powershell
$env:GITHUB_USER="your-github-username"
$env:GITHUB_TOKEN="ghp_your_personal_access_token_here"
```

**On Windows (Command Prompt):**
```cmd
set GITHUB_USER=your-github-username
set GITHUB_TOKEN=ghp_your_personal_access_token_here
```

**Option 3: Using Gradle Properties**

Add to your `gradle.properties` file in the project root:

```properties
GITHUB_USER=your-github-username
GITHUB_TOKEN=ghp_your_personal_access_token_here
```

**⚠️ Warning:** If using `gradle.properties`, make sure it's in `.gitignore` to avoid committing your token.

### Step 4: Sync Project

After adding the dependency and configuring credentials:

1. Click "Sync Now" in Android Studio, or
2. Run `./gradlew build --refresh-dependencies` from the command line

### Troubleshooting Installation

If you encounter issues:

- **401 Unauthorized**: Check that your `GITHUB_TOKEN` has `read:packages` permission
- **404 Not Found**: Verify the repository URL and that the package exists
- **Dependency not found**: Ensure credentials are set correctly in `local.properties` or environment variables

See [Troubleshooting Guide](./TROUBLESHOOTING.md) for more help.

---

## 🎯 Quick Start

### 1. Define Your State

Create a data class that implements `UIState`:

```kotlin
import com.debdut.library.composer.state.UIState
import com.debdut.library.composer.state.UIStateType
import com.debdut.library.composer.state.UIStateDefaultType
import com.debdut.library.composer.composer.ui.WidgetId

// Define Widget ID
object CounterWidgetId : WidgetId {
    override val id: String = "counter_widget"
}

// Define State
data class CounterState(
    override val widgetId: WidgetId = CounterWidgetId,
    override val visible: Boolean = true,
    override val type: UIStateType = UIStateDefaultType,
    val count: Int = 0
) : UIState
```

### 2. Create Actions

Define actions that will trigger state changes:

```kotlin
import com.debdut.library.composer.action.ActionId
import com.debdut.library.composer.action.StoreAction

// Define Action ID
object IncrementActionId : ActionId {
    override val id: String = "increment"
}

object DecrementActionId : ActionId {
    override val id: String = "decrement"
}

// Define Actions
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

Implement a Store to manage your widget's state:

```kotlin
import com.debdut.library.composer.store.Store
import com.debdut.library.composer.store.StoreId
import com.debdut.library.composer.store.StoreInitObj
import com.debdut.library.composer.store.StoreWidgetModel
import com.debdut.library.composer.action.ActionId
import com.debdut.library.composer.action.StoreAction

// Define Store ID
object CounterStoreId : StoreId {
    override val id: String = "counter_store"
}

// Define Init Model
data class InitModel : StoreInitObj

// Define Widget Model
data class WidgetModel : StoreWidgetModel {
    override val widgetId: String = CounterWidgetId.id
}

// Create Store
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

### 4. Create Store Factory

Create a factory to provide stores:

```kotlin
import com.debdut.library.composer.store.factory.StoreFactory

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

### 5. Create ViewModel

Create a ViewModel that extends the base composer ViewModel:

```kotlin
import androidx.lifecycle.ViewModel
import com.debdut.library.composer.composer.data.host.ListDataComposerHost
import com.debdut.library.composer.composer.data.ListDataComposer
import com.debdut.library.composer.composer.data.DataComposerActionHandler
import com.debdut.library.composer.composer.data.DataComposerActionHolder
import com.debdut.library.composer.action.Action
import com.debdut.library.composer.viewmodel.ListDataComposerViewModel

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

### 6. Use in Fragment

Implement the UIComposer interface in your Fragment:

```kotlin
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.debdut.library.composer.composer.ui.ListUIComposer
import com.debdut.library.composer.composer.data.host.ListDataComposerHost
import com.debdut.library.composer.composer.ui.syntax.observeAsState
import com.debdut.library.composer.composer.ui.syntax.observeAction
import com.debdut.library.composer.composer.ui.syntax.dispatch

class CounterFragment : Fragment(R.layout.fragment_counter), 
    ListUIComposer<CounterState, InitModel, WidgetModel> {
    
    private val viewModel: CounterViewModel by viewModels {
        CounterViewModelFactory(CounterStoreFactory())
    }
    
    override val container: ListDataComposerHost<CounterState, InitModel, WidgetModel>
        get() = viewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe state changes
        observeAsState(viewLifecycleOwner) { states ->
            states.firstOrNull()?.let { state ->
                binding.counterText.text = "Count: ${state.count}"
            }
        }
        
        // Handle UI actions (toasts, navigation, etc.)
        observeAction(viewLifecycleOwner) { holder ->
            when (val action = holder.action) {
                is ShowToastAction -> {
                    Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Initialize with widgets
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.init(
                widgets = listOf(CounterWidgetId),
                initData = InitModel()
            )
        }
        
        // Setup click listeners
        binding.incrementButton.setOnClickListener {
            dispatch(IncrementAction(amount = 1))
        }
        
        binding.decrementButton.setOnClickListener {
            dispatch(DecrementAction(amount = 1))
        }
    }
}
```

### 7. Dependency Injection (Optional)

If using dependency injection (e.g., Hilt, Koin), provide the StoreFactory:

**For Hilt:**

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

---

## 📚 Core Concepts

### Stores

Stores are the core business units that manage state for individual widgets. Each Store:

- Manages the UI state for one widget
- Processes actions and updates state accordingly
- Can dispatch side effects to parent layers
- Is easily testable in isolation

### Actions

Actions are immutable data classes that describe "what happened" in the system:

- **StoreAction**: Actions handled by Stores (most common)
- **DataComposerAction**: Actions handled by DataComposer layer
- **UIComposerAction**: Actions that bubble up to UI layer (navigation, toasts, etc.)

### Composers

Composers orchestrate multiple Stores:

- **DataComposer**: Manages Stores, combines states, routes actions
- **UIComposer**: Lifecycle-aware wrapper for UI layer integration

### State Management

- States are immutable data classes implementing `UIState`
- State updates flow reactively through Kotlin Flows
- Each widget has its own isolated state

---

## 📖 File Documentation

### Package Structure

The Composer library is organized into the following packages:

- **`com.debdut.library.composer.store`** - Store implementation and management
  - Core Store class and StoreId
  - StoreFactory for creating stores
  - Store lifecycle management

- **`com.debdut.library.composer.action`** - Action definitions and routing
  - StoreAction, DataComposerAction, UIComposerAction
  - ActionId and action holders
  - Action routing mechanisms

- **`com.debdut.library.composer.composer`** - Composer interfaces and implementations
  - DataComposer (Single, List, ListWithHeaderFooter variants)
  - UIComposer interfaces
  - Composer hosts and syntax extensions

- **`com.debdut.library.composer.state`** - State management interfaces
  - UIState interface and implementations
  - State type markers (Header, Footer, Default)

- **`com.debdut.library.composer.composer.ui`** - UI layer integration
  - WidgetId definitions
  - Base Fragment classes
  - Lifecycle-aware observation helpers

### Import Examples

```kotlin
// Store
import com.debdut.library.composer.store.Store
import com.debdut.library.composer.store.StoreId
import com.debdut.library.composer.store.StoreFactory

// Actions
import com.debdut.library.composer.action.StoreAction
import com.debdut.library.composer.action.ActionId

// Composers
import com.debdut.library.composer.composer.data.DataComposer
import com.debdut.library.composer.composer.ui.UIComposer

// State
import com.debdut.library.composer.state.UIState

// ViewModels
import com.debdut.library.composer.viewmodel.ListDataComposerViewModel
```

---

## 🧪 Testing

Stores can be easily tested in isolation:

```kotlin
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class CounterStoreTest {
    
    @Test
    fun `increment action increases count`() = runTest {
        val store = CounterStore()
        store.initialise(InitModel())
        
        store.receive(IncrementAction(amount = 5), CounterStoreId)
        
        assertEquals(5, store.currentState?.count)
    }
    
    @Test
    fun `decrement action decreases count`() = runTest {
        val store = CounterStore()
        store.initialise(InitModel())
        store.receive(IncrementAction(amount = 10), CounterStoreId)
        
        store.receive(DecrementAction(amount = 3), CounterStoreId)
        
        assertEquals(7, store.currentState?.count)
    }
}
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

Please read our [Contributing Guide](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

Quick steps:
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2024 Debdut Saha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 👤 Author

**Debdut Saha**

- Email: debdut.saha.1@gmail.com
- GitHub: [@12345debdut](https://github.com/12345debdut)

---

## 🙏 Acknowledgments

- Built with [Kotlin](https://kotlinlang.org/) and [Android Jetpack](https://developer.android.com/jetpack)
- Inspired by unidirectional data flow patterns
- Uses [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Flow](https://kotlinlang.org/docs/flow.html) for reactive programming

---

## 📊 Project Status

This project is actively maintained and ready for production use. Current version: **1.0.0**

---

## 📚 Additional Resources

- **[Full Documentation](#composer-module-documentation)** - Complete API reference and guides
- **[Publishing Guide](./PUBLISHING.md)** - How to publish your own libraries
- **[Troubleshooting](./TROUBLESHOOTING.md)** - Common issues and solutions
- **[Contributing Guide](./CONTRIBUTING.md)** - How to contribute to this project
- **[Changelog](./CHANGELOG.md)** - Version history and changes

---

<div align="center">

**⭐ If you find this project helpful, please consider giving it a star! ⭐**

Made with ❤️ for the Android community

</div>
