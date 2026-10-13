package dev.fbaldhagen.tectonic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.fbaldhagen.tectonic.navigation.AppNavKey
import dev.fbaldhagen.tectonic.navigation.TectonicEntryProviderInstaller
import dev.fbaldhagen.tectonic.splash.AppSplash
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryProviderInstallers:
            Set<@JvmSuppressWildcards TectonicEntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        AppSplash().install(splashScreen)

        enableEdgeToEdge()

        setContent {
            TectonicApp(
                startDestination = AppNavKey.Home,
                entryProviderInstallers = entryProviderInstallers,
            )
        }
    }
}