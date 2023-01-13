package expo.modules.sensorsbackground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
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


class ExpoSensorsBackgroundTaskConsumer(context: Context?, taskManagerUtils: TaskManagerUtilsInterface?) : TaskConsumer(context, taskManagerUtils),
    TaskConsumerInterface, LifecycleEventListener {
    private var expoServiceSensor:ExpoSensorService? = null
    private var mService : ExpoSensorTaskService? = null
    private var mTask: TaskInterface? = null
    private val TAG:String = "EXSensorsBgTaskConsumer"
    private val FOREGROUND_SERVICE_KEY = "foregroundService"
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
    override fun didRegister(task: TaskInterface?) {
        Log.w(TAG, "didRegister()")
        mTask = task;
        expoServiceSensor = ExpoSensorService();
        expoServiceSensor!!.start(context);
        maybeStartForegroundService()

    }

    override fun didUnregister() {
        Log.w(TAG, "didUnregister()")
        mTask = null;
        expoServiceSensor!!.stop()
    }



    private fun shouldUseForegroundService(options: Map<String,Object?>): Boolean {
        return options.containsKey(FOREGROUND_SERVICE_KEY);
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
//                    mService = (service as ExpoSensorTaskService.ServiceBinder).getService()
                    mService = ExpoSensorTaskService();
                    mService!!.setParentContext(context)
                    mService!!.startForeground(serviceOptions)
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    mService?.stop()
                    mService = null
                }
            }, Context.BIND_AUTO_CREATE)
        } else {
            Log.w(TAG, "Restart the service")
            // Restart the service with new service options.
            mService!!.startForeground(optionsMap.getArguments(FOREGROUND_SERVICE_KEY).toBundle())
        }
    }

    private fun stopForegroundService() {
        mService?.stop()

    }
}


