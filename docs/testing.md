---
title: Testing
nav_order: 6
---

# Testing

Stores are pure business logic with no Android dependencies, making them straightforward to test.

## Dependencies

```kotlin
// build.gradle.kts
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
```

## Testing a Store

```kotlin
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class CounterStoreTest {

    @Test
    fun `increment action increases count`() = runTest {
        val store = CounterStore()
        store.initialize(InitModel())

        store.receive(IncrementAction(amount = 5), CounterStoreId)

        assertEquals(5, store.currentState?.count)
    }

    @Test
    fun `decrement action decreases count`() = runTest {
        val store = CounterStore()
        store.initialize(InitModel())
        store.receive(IncrementAction(amount = 10), CounterStoreId)

        store.receive(DecrementAction(amount = 3), CounterStoreId)

        assertEquals(7, store.currentState?.count)
    }

    @Test
    fun `decrement does not go below zero`() = runTest {
        val store = CounterStore()
        store.initialize(InitModel())

        store.receive(DecrementAction(amount = 5), CounterStoreId)

        assertEquals(0, store.currentState?.count)
    }
}
```

## Testing with Turbine

[Turbine](https://github.com/cashapp/turbine) makes testing Kotlin Flows ergonomic:

```kotlin
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest

@Test
fun `state flow emits updates`() = runTest {
    val store = CounterStore()
    store.stateFlow.test {
        store.initialize(InitModel())
        val initial = awaitItem()
        assertEquals(0, initial.count)

        store.receive(IncrementAction(amount = 3), CounterStoreId)
        val updated = awaitItem()
        assertEquals(3, updated.count)

        cancelAndConsumeRemainingEvents()
    }
}
```

## Testing DataComposer interactions

Use MockK to verify action routing:

```kotlin
import io.mockk.coVerify
import io.mockk.mockk

@Test
fun `action is routed to correct store`() = runTest {
    val store = mockk<CounterStore>(relaxed = true)
    // set up composer with mocked store, dispatch action, then verify:
    coVerify { store.receive(any<IncrementAction>(), any()) }
}
```

## Tips

- Test Stores directly -- they don't need a ViewModel or UI
- Use `runTest` from `kotlinx-coroutines-test` for suspend functions
- Use Turbine for verifying Flow emissions over time
- Keep Store logic pure: no Android framework imports in Store classes
