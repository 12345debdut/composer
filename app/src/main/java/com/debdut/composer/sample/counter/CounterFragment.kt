package com.debdut.composer.sample.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.syntax.dispatch
import com.debdut.composer.composer.data.syntax.observeActions
import com.debdut.composer.composer.data.syntax.observeAsState
import com.debdut.composer.state.UIState

class CounterFragment : Fragment() {

    private val viewModel: CounterViewModel by viewModels()

    private lateinit var countText: TextView
    private lateinit var labelText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)

            labelText = TextView(context).apply {
                textSize = 24f
            }
            addView(labelText)

            countText = TextView(context).apply {
                textSize = 48f
            }
            addView(countText)

            val buttonRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            buttonRow.addView(Button(context).apply {
                text = "-"
                setOnClickListener { viewModel.dispatch(DecrementAction()) }
            })

            buttonRow.addView(Button(context).apply {
                text = "+"
                setOnClickListener { viewModel.dispatch(IncrementAction()) }
            })

            buttonRow.addView(Button(context).apply {
                text = "Reset"
                setOnClickListener { viewModel.dispatch(ResetAction()) }
            })

            addView(buttonRow)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialize()

        // Observe state changes
        viewModel.observeAsState(viewLifecycleOwner.lifecycleScope) {
            filterIsInstance<CounterState>().firstOrNull()?.let { state ->
                countText.text = state.count.toString()
                labelText.text = state.label
            }
        }

        // Observe UI side effects
        viewModel.observeActions(viewLifecycleOwner.lifecycleScope) {
            when (val action = this.action) {
                is ShowToastAction -> {
                    Toast.makeText(requireContext(), action.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
