package com.angelbroking.spark.libraries.composer.composer

/**
 * Base marker interface for all composers in the StoreComposer architecture.
 *
 * Composers are orchestration components that manage multiple [Store]s and
 * coordinate state and action flow between layers.
 *
 * ## Composer Hierarchy
 * ```
 * Composer
 * ├── DataComposer          - Manages Stores and state
 * │   ├── SingleDataComposer
 * │   ├── ListDataComposer
 * │   └── ListWithHeaderAndFooterDataComposer
 * │
 * └── UIComposer            - UI layer wrapper
 *     ├── SingleUIComposer
 *     ├── ListUIComposer
 *     └── ListUIComposerWithHeaderAndFooter
 * ```
 *
 * ## Architecture Role
 * - **DataComposer**: Holds stores, combines states, routes actions
 * - **UIComposer**: Provides lifecycle-aware observation for UI layer
 *
 * @see DataComposer
 * @see UIComposer
 */
interface Composer