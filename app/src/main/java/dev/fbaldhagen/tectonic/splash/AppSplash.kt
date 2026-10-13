package dev.fbaldhagen.tectonic.splash

import android.os.Build
import android.os.Process
import android.os.SystemClock
import androidx.core.splashscreen.SplashScreen

internal class AppSplash {

    fun install(splashScreen: SplashScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setKeepOnScreenCondition {
                millisSinceProcessStart() < MINIMUM_SPLASH_DURATION
            }
        }

        splashScreen.setOnExitAnimationListener { splash ->
            splash.view.animate()
                .alpha(0f)
                .setDuration(EXIT_DURATION)
                .withEndAction { splash.remove() }
                .start()
        }
    }

    private fun millisSinceProcessStart(): Long =
        SystemClock.elapsedRealtime() - Process.getStartElapsedRealtime()

    private companion object {
        const val MINIMUM_SPLASH_DURATION = 1_050L
        const val EXIT_DURATION = 200L
    }
}