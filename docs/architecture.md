---
title: Architecture
nav_order: 3
---

# Architecture

Composer follows a unidirectional data flow pattern with three layers.

## Layer diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI Layer                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  Fragment / Activity / Composable                           ││
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
│  │  DataComposer (Single / List / ListWithHeaderFooter)        ││
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

## Data flow

1. **User interaction** -- UI dispatches a `StoreAction`
2. **Action routing** -- DataComposer routes the action to the appropriate Store(s)
3. **State update** -- Store processes the action and updates its state
4. **State emission** -- new state flows back through DataComposer
5. **UI update** -- UIComposer receives combined state and renders UI

## Components at a glance

| Component | Role |
|-----------|------|
| **Store** | Manages state for a single widget; processes actions |
| **DataComposer** | Orchestrates multiple Stores; combines their states; routes actions |
| **UIComposer** | Lifecycle-aware wrapper that connects the UI layer to the data layer |
| **Action** | Immutable data class describing "what happened" |
| **UIState** | Immutable state object representing widget state |

## DataComposer variants

| Variant | Use case |
|---------|----------|
| `SingleDataComposer` | Single-widget screens |
| `ListDataComposer` | Multi-widget screens (flat list of states) |
| `ListWithHeaderAndFooterDataComposer` | Multi-widget screens with distinct header and footer state |

## Package overview

| Package | Contents |
|---------|----------|
| `com.debdut.composer.store` | Store, StoreId, StoreFactory, lifecycle management |
| `com.debdut.composer.action` | StoreAction, DataComposerAction, UIComposerAction, ActionId, holders |
| `com.debdut.composer.composer.data` | DataComposer interfaces and implementations |
| `com.debdut.composer.composer.ui` | UIComposer interfaces, WidgetId, base Fragment classes |
| `com.debdut.composer.state` | UIState, UIStateType, Header/Footer markers |
| `com.debdut.composer.viewmodel` | ViewModel base classes |
| `com.debdut.composer.compose` | Jetpack Compose extensions (separate artifact) |
