package view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import viewmodel.SettingsViewModel

@AndroidEntryPoint
internal class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setContent {
                HolocanonTheme {
                    SettingsScreen()
                }
            }
        }

    @Composable
    private fun SettingsScreen() {
        Column{
            UserDefinedFilter(1)
        }
    }

    @Composable
    private fun UserDefinedFilter(number: Int) {
        Text("User Defined Filter $number", color = MaterialTheme.colorScheme.primary)
    }
}
