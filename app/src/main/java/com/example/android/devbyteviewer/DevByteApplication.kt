package com.example.android.devbyteviewer

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import androidx.work.*
import com.example.android.devbyteviewer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class DevByteApplication : Application() {

	private val coroutineContext = Dispatchers.Default
	private val applicationCoroutineScope = CoroutineScope(coroutineContext)

	/**
	 * onCreate is called before the first screen is shown to the user.
	 *
	 * Use it to setup any background tasks, running expensive setup operations in a background
	 * thread to avoid delaying app start.
	 */
	override fun onCreate() {
		super.onCreate()
		Timber.plant(Timber.DebugTree())
		delayInit()
	}

	private fun delayInit() {

		val constraints = Constraints.Builder()
				.setRequiredNetworkType(NetworkType.UNMETERED)
				.setRequiresBatteryNotLow(true)
				.setRequiresCharging(true)
				.apply {
					if (SDK_INT >= M)
						setRequiresDeviceIdle(true)
				}
				.build()

		applicationCoroutineScope.launch {
			val workRepeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
					1, TimeUnit.DAYS)
					.setConstraints(constraints)
					.build()

			// soon after the request is enqueued, it will start running, it will do the work.
			WorkManager.getInstance().enqueueUniquePeriodicWork(
					RefreshDataWorker.WORK_NAME,
					ExistingPeriodicWorkPolicy.KEEP,
					workRepeatingRequest
			)
		}

	}
}
