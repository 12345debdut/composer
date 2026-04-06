# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

---

## [1.0.0] - 2026-04-06

### Added

#### `composer` — Core module
- `Store` abstract base class: generic unidirectional state management unit per widget
- `StoreId`, `StoreInitObj`, `StoreWidgetModel`, `StoreFactory` — store identity and lifecycle contracts
- `StoreSyntax` DSL: `updateState {}`, `emitState {}`, `currentState`, `dispatch()`, `suspendDispatch()`
- `Action`, `ActionId`, `StoreAction`, `DataComposerAction`, `UIComposerAction` — full action type hierarchy
- `ActionHolder`, `StoreActionHolder`, `DataComposerActionHolder`, `UIComposerActionHolder` — typed action wrappers
- `UIState`, `UIStateType`, `UIStateDefaultType`, `HeaderUIStateType`, `FooterUIStateType`, `NoOpsUIState` — state hierarchy
- `WidgetId`, `HostWidgetId`, `ChildWidgetId`, `NoStoreWidgetId`, `GroupWidget` — widget identity system with parent-child visibility
- `DataComposer`, `SingleDataComposer`, `ListDataComposer`, `ListWithHeaderAndFooterDataComposer` — composer interfaces
- `DataComposerActionHandler` — cross-widget coordination callback
- `SingleDataComposerImpl`, `ListDataComposerImpl`, `ListWithHeaderFooterDataComposerImpl` — production-ready implementations (thread-safe with `Mutex` and `ConcurrentHashMap`)
- `DataComposerHost` hierarchy for ViewModel-layer access to composer state
- `ListDataComposerViewModel`, `ListWithHeaderAndFooterDataComposerViewModel` — base ViewModels with `viewModelScope` integration
- `ListUIComposerFragment`, `ListUIComposerWithHeaderAndFooterFragment` — base Fragments with lifecycle-aware `repeatOnLifecycle` observation
- `UIComposerSyntax` DSL: `dispatch()`, `observeAsState()`, `observeAction()`
- `DataComposerHostSyntax` DSL: `init()`, `updateWidget()`, `suspendDispatch()`, `suspendBatchDispatch()`, `observeAsState()`, `observeActions()`
- `HostExtensions`: factory functions `listDataComposer()`, `singleDataComposer()`, `listWithHeaderAndFooterDataComposer()`
- Binary compatibility validation via `binary-compatibility-validator` plugin
- Unit tests for `Store`, `SingleDataComposerImpl`, `ListDataComposerImpl`

#### `composer-compose` — Jetpack Compose extensions module
- `collectAsState()` — lifecycle-aware `State<List<UIState>>` collection from `DataComposerHost`
- `collectAsStateNoLifecycle()` — lifecycle-agnostic variant for previews and tests
- `CollectSideEffect()` — composable for safely collecting `UIComposerActionHolder` side effects via `LaunchedEffect`
- `uiStateFlow` / `uiActionFlow` — direct `StateFlow` / `SharedFlow` access extensions

#### `composer-bom` — Bill of Materials
- Single BOM artifact to manage consistent versions across `composer` and `composer-compose`

#### Publishing & CI/CD
- Maven Central publishing via Vanniktech Maven Publish plugin (`io.github.debdutsaha` group)
- `composer`, `composer-compose`, and `composer-bom` published atomically on GitHub Release
- GitHub Actions CI: build + test + `apiCheck` for all library modules on every PR
- GitHub Actions publish: tests + `apiCheck` gate before all three artifacts are published
- JitPack support via `jitpack.yml`
- Kotlin Explicit API mode enforced on both library modules
- GPG signing for all Maven Central publications

#### Sample app
- Counter widget demonstrating `Store`, `ListDataComposerViewModel`, `ListUIComposerFragment`

---

[Unreleased]: https://github.com/12345debdut/composerlibrary/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/12345debdut/composerlibrary/releases/tag/v1.0.0
