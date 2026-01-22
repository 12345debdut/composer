package com.angelbroking.spark.libraries.composer.store

/**
 * Interface for widget configuration models that can be identified by a widget ID.
 *
 * StoreWidgetModel is used as the third type parameter in [Store] for
 * widget-specific configuration or template data. This allows Stores to
 * work with widget templates and configurations from external sources.
 *
 * ## Usage Pattern
 * ```kotlin
 * // Define a widget model for template-driven configuration
 * data class OrderWidgetTemplateModel(
 *     override val widgetId: String,
 *     val displayConfig: DisplayConfig,
 *     val validationRules: List<ValidationRule>,
 *     val featureFlags: Map<String, Boolean>
 * ) : StoreWidgetModel
 *
 * // Store can use this for configuration
 * class MyStore : Store<MyState, InitModel, OrderWidgetTemplateModel>() {
 *     // ...
 * }
 * ```
 *
 * ## Common Use Cases
 * - Server-driven UI configuration
 * - Feature flag driven widget behavior
 * - Template-based widget rendering
 * - A/B testing configurations
 *
 * @property widgetId String identifier linking this model to a widget
 *
 * @see Store
 * @see StoreInitObj
 */
interface StoreWidgetModel {
    val widgetId: String
}