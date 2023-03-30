package expo.modules.sensorsbackground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import expo.modules.core.arguments.MapArguments
import expo.modules.core.arguments.ReadableArguments
import expo.modules.core.interfaces.LifecycleEventListener
import expo.modules.interfaces.taskManager.TaskConsumer
import expo.modules.interfaces.taskManager.TaskConsumerInterface
import expo.modules.interfaces.taskManager.TaskInterface
import expo.modules.interfaces.taskManager.TaskManagerUtilsInterface
import expo.modules.sensorsbackground.services.ExpoSensorService
import expo.modules.sensorsbackground.services.ExpoSensorTaskService
import kotlinx.coroutines.*
import okhttp3.*
import java.io.Serializable
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt


class ExpoSensorsBackgroundTaskConsumer(context: Context?, taskManagerUtils: TaskManagerUtilsInterface?) : TaskConsumer(context, taskManagerUtils),
    TaskConsumerInterface, LifecycleEventListener {
    private var expoServiceSensor:ExpoSensorService? = null;
    private var mService : ExpoSensorTaskService? = null
    private var mTask: TaskInterface? = null
    private val TAG:String = "EXSensorsBgTaskConsumer"
    private val FOREGROUND_SERVICE_KEY = "foregroundService"
    private var lastCurrent:Long =0;
    private val client = OkHttpClient()
    override fun onHostResume() {
        Log.w(TAG, "onHostResume()")
    }

    override fun onHostPause() {
        Log.w(TAG, "onHostPause()")
    }

    override fun onHostDestroy() {
        Log.w(TAG, "onHostDestroy()")
    }

    override fun taskType(): String {
        Log.w(TAG, "taskType()")
        return "sensors"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun didRegister(task: TaskInterface?) {
        Log.w(TAG, "didRegister()")
        mTask = task
        expoServiceSensor = ExpoSensorService(context)
        startSensorUpdates()
        maybeStartForegroundService()
    }

    override fun didUnregister() {
        Log.w(TAG, "didUnregister()")
        mTask = null
        stopForegroundService()
        stopSensorUpdates()
    }

    private fun shouldUseForegroundService(options: Map<String,Object?>): Boolean {
        return options.containsKey(FOREGROUND_SERVICE_KEY)
    }

    private fun maybeStartForegroundService() {
        Log.d(TAG, "maybeStartForegroundService()")
        // Foreground service is available as of Android Oreo.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val optionsMap:ReadableArguments = MapArguments(mTask!!.options)
        Log.d(optionsMap::class.simpleName,"maybeStartForegroundService")
        val context = context
        val useForegroundService: Boolean = shouldUseForegroundService(mTask!!.options as Map<String, Object?>)
        if (context == null) {
            Log.w(TAG, "Context not found when trying to start foreground service.")
            return
        }

        // Service is already running, but the task has been registered again without `foregroundService` option.
        if (mService != null && !useForegroundService) {
            stopForegroundService()
            return
        }

        // Service is not running and the user don't want to start foreground service.
        if (!useForegroundService) {
            return
        }

        // Foreground service is requested but not running.
        if (mService == null) {
            Log.w(TAG, "Foreground service is requested")

            val serviceIntent = Intent(context, ExpoSensorTaskService::class.java)
            val extras = Bundle()
            val serviceOptions: Bundle = optionsMap.getArguments(FOREGROUND_SERVICE_KEY).toBundle()
            // extras param name is appId for legacy reasons
            extras.putString("appId", mTask!!.appScopeKey)
            extras.putString("taskName", mTask!!.name)
            extras.putBoolean(
                "killService",
                serviceOptions.getBoolean("killServiceOnDestroy", false)
            )
            serviceIntent.putExtras(extras)
            context.startForegroundService(serviceIntent)
            context.bindService(serviceIntent, object : ServiceConnection {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    mService = (service as ExpoSensorTaskService.LocalBinder).getService()
                    mService?.setParentContext(context)
                    mService?.startForeground(serviceOptions)
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    mService?.stop()
                    mService = null
                }
            }, Context.BIND_AUTO_CREATE)
        } else {
            Log.w(TAG, "Restart the service")
            // Restart the service with new service options.
            mService?.startForeground(optionsMap.getArguments(FOREGROUND_SERVICE_KEY).toBundle())
        }
    }

    private fun stopForegroundService() {
        mService?.stop()
    }
    private fun mapToBundle(map: Map<String, Any>): Bundle {
        val result = Bundle()
//        if (map == null) return result
        for (key in map.keys) {
            result.putSerializable(key, map[key] as Serializable?)
        }
        return result
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startSensorUpdates(){
        val mUpdateInteval:Int? = mTask!!.options.get("timeInterval").toString().toDouble().roundToInt()

        expoServiceSensor!!.addListener{ data: SensorData ->
            val current = System.currentTimeMillis()
            val map : Map<String,Any> = mapOf("x" to data.x,"y" to data.y,"z" to data.z)
//            val sensitive = sqrt(data.x * data.x + data.y * data.y + data.z * data.z)

//            if(sensitive > 50) {
//                val mediaType = "application/json; charset=utf-8".toMediaType()
//                val jsonObject = JSONObject()
//                try {
//                    jsonObject.put("Description", "Shake Detected from Native")
//                    jsonObject.put("x", data.x)
//                    jsonObject.put("y", data.y)
//                    jsonObject.put("z", data.z)
//                    jsonObject.put("acceleration", sensitive)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                val request = Request.Builder()
//                        .url("https://1db0-113-161-71-173.ap.ngrok.io")
//                        .addHeader("Content-Type", "application/json")
//                        .post(jsonObject.toString().toRequestBody(mediaType))
//                        .build()
//
//                client.newCall(request).enqueue(object : Callback {
//                    override fun onFailure(call: Call, e: IOException) {
//                        e.printStackTrace()
//                    }
//
//                    override fun onResponse(call: Call, response: Response) {
//                        response.use {
//                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                            for ((name, value) in response.headers) {
//                                println("$name: $value")
//                            }
//
//                            println(response.body!!.string())
//                        }
//                    }
//                })
//            }

            if((current - lastCurrent) > mUpdateInteval!!){
                mTask!!.execute(mapToBundle(map!!), null)

                lastCurrent = current
            }


        }
        expoServiceSensor!!.delaySensor(mTask!!.options.get("timeInterval") as Number)
        expoServiceSensor!!.start()
    }

    private fun stopSensorUpdates(){
        expoServiceSensor!!.removeListener()
        expoServiceSensor!!.stop()
    }
}