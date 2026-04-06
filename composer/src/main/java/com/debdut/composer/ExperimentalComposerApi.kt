package com.debdut.composer

/**
 * Marks declarations that are **experimental** in the Composer API.
 *
 * Experimental APIs may change or be removed in future releases without notice.
 * Consumers must opt in via `@OptIn(ExperimentalComposerApi::class)`.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is experimental and may change in future releases."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY
)
public annotation class ExperimentalComposerApi
