package expo.modules.sensorsbackground.services

import android.app.*
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi


class ExpoSensorTaskService:Service() {
    private val TAG:String = "SensorTaskService"
    private var mServiceId:Int = 1234
    private var mChannelId:String = "SensorBackground"
    private var mParentContext: Context? = null
    private var mKillService = false
    private var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() : ExpoSensorTaskService {
            return this@ExpoSensorTaskService
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder;
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val extras = intent.extras
        if (extras != null) {
            mChannelId = extras.getString("appId") + ":" + extras.getString("taskName")
            mKillService = extras.getBoolean("killService", false)
        }
        return START_REDELIVER_INTENT
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (mKillService) {
            super.onTaskRemoved(rootIntent)
            stop();
        }
    }

    fun stop() {
        stopForeground(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startForeground(serviceOptions: Bundle?) {
        Log.w("ExpoSensorTaskSer","startForeground()")
        val notification: Notification? = buildServiceNotification(serviceOptions)
        startForeground(mServiceId, notification)
    }


    fun setParentContext(context: Context) {
        // Background Sensor logic is still outside ExpoSensorTaskService,
        // so we have to save parent context in order to make sure it won't be destroyed by the OS.
        mParentContext = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prepareChannel(id: String) {
        val notificationManager =
            getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            val appName = applicationInfo.loadLabel(packageManager).toString()
            var channel = notificationManager.getNotificationChannel(id)
            if (channel == null) {
                channel = NotificationChannel(id, appName, NotificationManager.IMPORTANCE_LOW)
                channel.description = "Background sensor notification channel"
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildServiceNotification(serviceOptions: Bundle?): Notification? {
        Log.w("ExpoSensorTaskSer","buildServiceNotification()")
        prepareChannel(mChannelId)
        val builder = Notification.Builder(this, mChannelId)
        val title = serviceOptions?.getString("notificationTitle")
        val body = serviceOptions?.getString("notificationBody")
        val color: Int? = colorStringToInteger(serviceOptions?.getString("notificationColor"))
        if (title != null) {
            builder.setContentTitle(title)
        }
        if (body != null) {
            builder.setContentText(body)
        }
        if (color != null) {
            builder.setColorized(true).setColor(color)
        } else {
            builder.setColorized(false)
        }
        val intent: Intent? = mParentContext!!.packageManager
            .getLaunchIntentForPackage(mParentContext!!.packageName)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            // We're defaulting to the behaviour prior API 31 (mutable) even though Android recommends immutability
            val mutableFlag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            val contentIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or mutableFlag
            )
            builder.setContentIntent(contentIntent)
        }
        return builder.setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(applicationInfo.icon)
            .build()
    }

    private fun colorStringToInteger(color: String?): Int? {
        return try {
            Color.parseColor(color)
        } catch (e: Exception) {
            null
        }
    }
}