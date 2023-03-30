package expo.modules.sensorsbackground.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context;
import android.content.Context.ALARM_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build
import expo.modules.sensorsbackground.SensorData
import java.util.Calendar


class ExpoSensorService(context: Context) {
    private var senAccelerometer: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var alarmManager: AlarmManager? = null
    private var listenerData:((data: SensorData)-> Unit)? = null
    private var reactContext: Context? = context
    private var delay: Int? = 0;

    fun delaySensor(delayNumber: Number){
        delay = when(delayNumber){
            0-> SensorManager.SENSOR_DELAY_FASTEST
            1-> SensorManager.SENSOR_DELAY_GAME
            2-> SensorManager.SENSOR_DELAY_UI
            3-> SensorManager.SENSOR_DELAY_NORMAL
            else->{
                SensorManager.SENSOR_DELAY_NORMAL
            }
        }
    }

    fun start() {
        if (sensorManager == null) {
            sensorManager = reactContext!!.getSystemService(SENSOR_SERVICE) as SensorManager
        }
        if (senAccelerometer == null) {
            senAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        sensorManager!!.registerListener(listener, senAccelerometer, delay!!)
        registerAlarm();
    }

    fun stop() {
        sensorManager?.unregisterListener(listener)
        unRegisterAlarm();
    }

    fun addListener(sensor :(data: SensorData)-> Unit){
        listenerData = sensor
    }

    fun removeListener() {
        listenerData = null;
    }

    private val listener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            listenerData?.invoke(SensorData(x,y,z))

        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // do nothing
        }
    }

    private fun registerAlarm() {
        val alarmPendingIntent = getPendingIntent(1122);
        if (alarmPendingIntent != null) {
            alarmManager = reactContext!!.getSystemService(ALARM_SERVICE) as AlarmManager;
            val calendar = Calendar.getInstance();
            calendar.timeInMillis = System.currentTimeMillis();
            calendar.add(Calendar.MINUTE, 1);
            alarmManager!!.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmPendingIntent
            )
        }
    }

    private fun unRegisterAlarm() {
        val alarmPendingIntent = getPendingIntent(1122);
        if(alarmPendingIntent != null) {
            alarmManager?.cancel(alarmPendingIntent)
        }
    }

    private fun getPendingIntent(requestCode: Int): PendingIntent? {
        val intent: Intent? = reactContext!!.packageManager
                .getLaunchIntentForPackage(reactContext!!.packageName)
        if(intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            // We're defaulting to the behaviour prior API 31 (mutable) even though Android recommends immutability
            val mutableFlag =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            return PendingIntent.getActivity(
                    reactContext!!,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or mutableFlag
            )
        }
        return null;
    }
}


