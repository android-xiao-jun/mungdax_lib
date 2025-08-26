package com.allo.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

object JobScheduler {

    private val mSingleWorker: ExecutorService = Executors.newCachedThreadPool()
    val mUIHandler: Handler = Handler(Looper.getMainLooper())

    fun serialJob(job: () -> Unit) {
        mSingleWorker.execute { job.invoke() }
    }

    fun <T> submitSerial(job: Callable<T>?): Future<T> {
        return mSingleWorker.submit(job)
    }

    fun uiJob(runnable: Runnable) {
        mUIHandler.post (runnable)
    }

    fun uiJobDelay(runnable: Runnable, delayMillis: Long) {
        mUIHandler.postDelayed(runnable, delayMillis)
    }
}