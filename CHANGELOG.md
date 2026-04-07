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

#### `composer-fragment` — Fragment extensions module
- `ListUIComposerFragment` — base Fragment for list-based screens with lifecycle-aware `repeatOnLifecycle` observation wired up
- `ListUIComposerWithHeaderAndFooterFragment` — base Fragment for list screens with fixed header and footer widgets
- `UIComposerSyntax` extensions for Fragment contexts: `dispatch()`, `observeAsState()`, `observeAction()`

#### `composer-bom` — Bill of Materials
- Single BOM artifact that manages consistent versions across `composer`, `composer-compose`, and `composer-fragment`

#### Publishing & CI/CD
- Maven Central publishing via plain `maven-publish` + `signing` plugins, driven by a `buildSrc` precompiled script plugin (`publish-convention.gradle.kts`) for convention-sharing across all Android library modules
- Published to Maven Central under the `io.github.12345debdut` group through the Sonatype **Central Publisher Portal** (`ossrh-staging-api.central.sonatype.com`)
- All four artifacts (`composer`, `composer-compose`, `composer-fragment`, `composer-bom`) published atomically on GitHub Release
- GitHub Actions CI: build + test + `apiCheck` for all library modules on every PR
- GitHub Actions publish workflow: tests + `apiCheck` gate, tag-vs-`gradle.properties` version drift guard, and a diagnostic step that fails fast if any required signing secret is missing
- GPG signing for all Maven Central publications via in-memory ASCII-armored key (`useInMemoryPgpKeys`)
- Kotlin `explicitApi()` mode enforced on all three library modules
- Reproducible archives (stripped timestamps, stable file ordering) so the same source produces byte-identical AAR/JAR/POM artifacts across builds
- Real Dokka-generated javadoc JAR attached to publications when the module applies the Dokka plugin, empty javadoc JAR as fallback
- GitHub Pages documentation site deployed via `docs.yml` workflow (Jekyll + just-the-docs theme + Mermaid diagrams)

#### Sample app
- Counter widget demonstrating `Store`, `ListDataComposerViewModel`, `ListUIComposerFragment`

---

[Unreleased]: https://github.com/12345debdut/composer/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/12345debdut/composer/releases/tag/v1.0.0
