package com.hermes.android.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.presentation.ui.theme.SystemBarsTheme
import com.hermes.android.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HermesTheme {
                SystemBarsTheme(activity = this)
                Surface {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}