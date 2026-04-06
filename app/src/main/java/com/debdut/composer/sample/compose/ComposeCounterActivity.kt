package com.debdut.composer.sample.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.debdut.composer.compose.CollectSideEffect
import com.debdut.composer.compose.collectAsState
import com.debdut.composer.sample.counter.CounterState
import com.debdut.composer.sample.counter.CounterViewModel
import com.debdut.composer.sample.counter.DecrementAction
import com.debdut.composer.sample.counter.IncrementAction
import com.debdut.composer.sample.counter.ResetAction
import com.debdut.composer.sample.counter.ShowToastAction

/**
 * Compose-based counter screen demonstrating the composer-compose module.
 *
 * Reuses the same [CounterViewModel] and Store as the Fragment-based sample,
 * but observes state via [collectAsState] and handles side effects via [CollectSideEffect].
 */
class ComposeCounterActivity : ComponentActivity() {

    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initialize()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CounterScreen(viewModel)
                }
            }
        }
    }
}

@Composable
private fun CounterScreen(viewModel: CounterViewModel) {
    val states by viewModel.collectAsState()
    val context = LocalContext.current

    val counterState = states.filterIsInstance<CounterState>().firstOrNull()

    CollectSideEffect(viewModel) { holder ->
        when (val action = holder.action) {
            is ShowToastAction -> {
                Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = counterState?.label ?: "Counter",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${counterState?.count ?: 0}",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(onClick = { viewModel.container.dispatch(DecrementAction()) }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = { viewModel.container.dispatch(ResetAction()) }) {
                Text("Reset")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { viewModel.container.dispatch(IncrementAction()) }) {
                Text("+")
            }
        }
    }
}
