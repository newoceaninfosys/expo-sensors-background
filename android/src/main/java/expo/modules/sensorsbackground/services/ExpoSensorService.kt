package expo.modules.sensorsbackground.services

import android.content.Context;
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


class ExpoSensorService {
  private var senAccelerometer: Sensor? = null
  private var sensorManager: SensorManager? = null
//    private var mNotificationManager: NotificationManager? = null

//   override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//       super.onStartCommand(intent, flags, startId)
//       Log.d(TAG, "onStartCommand")
//       mNotificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//       val builder = buildNotification()
//       startForeground(ID_SERVICE, builder.build())
//       return START_STICKY
//   }
    fun start(reactContext: Context?){
    sensorManager = reactContext?.getSystemService(SENSOR_SERVICE) as SensorManager
    senAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    sensorManager!!.registerListener(listener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
    fun stop(){
        if (sensorManager != null) {
           sensorManager!!.unregisterListener(listener)
       }
    }
//   override fun onCreate() {
////       Log.d(TAG, "onCreate")
//       sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//       senAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//       sensorManager!!.registerListener(listener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//   }

//   override fun onDestroy() {
//       super.onDestroy()
//       if (sensorManager != null) {
//           sensorManager!!.unregisterListener(listener)
//       }
//     //   stopForeground(true)
//   }

//   override fun onBind(intent: Intent): IBinder? {
//       return null
//   }
//   private fun buildNotification(): Notification.Builder {
//       val builder = Notification.Builder(this)
//       builder.setSmallIcon(R.mipmap.ic_launcher)
//       builder.setContentTitle("App is running....")
//       val intentClass = mainActivityClass
//       val intent = Intent(mainContext, intentClass)
//       intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
//               or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//       @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//       builder.setContentIntent(pendingIntent)
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           val channel = NotificationChannel("My Notification Channel ID",
//                   "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT)
//           channel.description = "My Notification Channel Description"
//           // Register the channel with the system; you can't change the importance
//           // or other notification behaviors after this
//           mNotificationManager!!.createNotificationChannel(channel)
//           builder.setChannelId(channel.id)
//       }
//       return builder
//   }

   private val listener: SensorEventListener = object : SensorEventListener {
       private var lastUpdate: Long = 0
       private var last_x = 0f
       private var last_y = 0f
       private var last_z = 0f
       private val SHAKE_THRESHOLD = 600
       override fun onSensorChanged(event: SensorEvent) {
           // The acceleration may be negative, so take their absolute value
           val x = Math.abs(event.values[0])
           val y = Math.abs(event.values[1])
           val z = Math.abs(event.values[2])
           val curTime = System.currentTimeMillis()
           if (curTime - lastUpdate > 100) {
               val diffTime = curTime - lastUpdate
               lastUpdate = curTime
               val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
               if (speed > SHAKE_THRESHOLD) {
//                   Toast.makeText(mainContext, "shaked", Toast.LENGTH_SHORT).show()
                   Log.d("TYPE_ACCELEROMETER", "shakeddd")
               }
               last_x = x
               last_y = y
               last_z = z
           }
       }

       override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
           // do nothing
       }
   }
}

//   companion object {
//       private const val ID_SERVICE = 199
//   }
// }

