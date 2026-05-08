package dev.squatedev.nestmanager.TaskSync

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Build

class CustomAsync private constructor(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: CustomAsync? = null

        fun getInstance(context: Context): CustomAsync {
            return instance ?: synchronized(this) {
                instance ?: CustomAsync(context.applicationContext).also { instance = it }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun <Params, Progress, Result> executeAsync(
        taskName: String = "AsyncTask_${System.currentTimeMillis()}",
        backgroundTask: () -> Result,
        onPreExecute: (() -> Unit)? = null,
        onPostExecute: (Result) -> Unit,
        onProgressUpdate: ((Int) -> Unit)? = null,
        onCancelled: (() -> Unit)? = null
    ): AsyncTask<Params, Progress, Result> {
        val task = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Params, Progress, Result>() {
            override fun onPreExecute() {
                super.onPreExecute()
                onPreExecute?.invoke()
            }

            @Deprecated("Deprecated in Java")
            override fun doInBackground(vararg params: Params?): Result {
                return backgroundTask()
            }

            @Deprecated("Deprecated in Java")
            override fun onPostExecute(result: Result) {
                super.onPostExecute(result)
                onPostExecute(result)
            }

            @Deprecated("Deprecated in Java")
            override fun onProgressUpdate(vararg values: Progress?) {
                super.onProgressUpdate(*values)
                values.firstOrNull()?.let { progress ->
                    (progress as? Int)?.let { onProgressUpdate?.invoke(it) }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onCancelled() {
                super.onCancelled()
                onCancelled?.invoke()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            task.execute()
        }

        return task
    }
}