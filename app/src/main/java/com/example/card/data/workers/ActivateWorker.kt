package com.example.card.data.workers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.card.App
import com.example.card.R
import com.example.card.data.api.RetrofitBuilder
import com.example.card.data.model.ScratchCardState
import com.example.card.data.preferences.CardDataStoreImpl
import com.example.card.repository.ApiRepositoryImpl
import com.example.card.repository.CardRepositoryImpl
import com.example.card.ui.MainActivity
import kotlinx.coroutines.flow.catch

class ActivateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val KEY_CODE = "key_code"
        const val ACTIVATE_WORK_NAME = "activate_work_name"
        const val WORKER_TAG = "worker_tag"

        private const val NOTIFICATION_ID = 756

        fun getWorker(code: String) =
             OneTimeWorkRequestBuilder<ActivateWorker>()
                .setInputData(workDataOf(KEY_CODE to code))
                 .addTag(WORKER_TAG)
                .build()
    }

    private val apiRepository = ApiRepositoryImpl(RetrofitBuilder.apiService)
    private val cardRepository = CardRepositoryImpl(CardDataStoreImpl())

    override suspend fun doWork(): Result {
        val code = inputData.getString(KEY_CODE) ?: return Result.failure()
        showNotification()
        var result = Result.failure()
        apiRepository.isScratchCardActivated(code)
            .catch { e ->
                showFinishNotification(false)
            }
            .collect {
                if (it) {
                    cardRepository.setCardState(ScratchCardState.ACTIVATED)
                    showFinishNotification(true)
                    result = Result.success()
                } else {
                    cardRepository.setCardState(ScratchCardState.SCRATCHED)
                    showFinishNotification(false)
                }
            }
        return result
    }

    private fun showFinishNotification(success: Boolean) {
        val text = if (success) {
            R.string.notification_description_successfully
        } else {
            R.string.notification_description_not_successfully
        }
        val builder = NotificationCompat
            .Builder(context, App.CHANNEL_ID)
            .setContentText(context.getString(text))
            .setSmallIcon(R.drawable.baseline_cloud_upload_24)
        updateNotification(builder)
    }

    private fun showNotification() {
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat
            .Builder(context, App.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_cloud_upload_24)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_description))
            .setProgress(0, 0, true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)
        updateNotification(builder)


    }

    private fun updateNotification(builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}