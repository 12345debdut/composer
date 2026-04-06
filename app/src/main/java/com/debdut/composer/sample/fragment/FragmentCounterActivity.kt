package com.debdut.composer.sample.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.debdut.composer.sample.counter.CounterFragment

class FragmentCounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, CounterFragment())
                .commit()
        }
    }
}
