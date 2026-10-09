package dev.fbaldhagen.tectonic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @javax.inject.Inject
    lateinit var entryProviderInstallers:
            Set<@JvmSuppressWildcards TectonicEntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TectonicApp(
                startDestination = AppNavKey.Home,
                entryProviderInstallers = entryProviderInstallers,
            )
        }
    }
}