package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.VideoDatabase.Companion.getInstance
import com.example.android.devbyteviewer.repository.VideosRepository
import retrofit2.HttpException


class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
		CoroutineWorker(appContext, params) {

	companion object {
		const val WORK_NAME = "RefreshDataWorker"
	}

	override suspend fun doWork(): Result {

		// The code in here, runs in the background {coroutines}
		val database = getInstance(applicationContext)
		val repository = VideosRepository(database)

		return try {
			repository.refreshVideos()
			Result.success()
		} catch (e: HttpException) {
			Result.retry()
		}
	}
}