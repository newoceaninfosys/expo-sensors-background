package expo.modules.sensorsbackground.services

import android.content.Context;
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import expo.modules.sensorsbackground.SensorData





class ExpoSensorService {
    private var senAccelerometer: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var listenerData:((data: SensorData)-> Unit)? = null;

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
    fun start(reactContext: Context?) {
        sensorManager = reactContext?.getSystemService(SENSOR_SERVICE) as SensorManager
        senAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(listener, senAccelerometer, delay!!)
    }

    fun stop() {
        if (sensorManager != null) {

            sensorManager!!.unregisterListener(listener)
        }
    }
    fun addListener(sensor :(data: SensorData)-> Unit){
        listenerData = sensor
    }
    fun removeListener(){
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
}


