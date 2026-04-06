package com.debdut.composer.sample.counter

import com.debdut.composer.store.StoreInitObj

data class CounterInitObj(
    val initialCount: Int = 0,
    val label: String = "Counter"
) : StoreInitObj
