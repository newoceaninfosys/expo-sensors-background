package expo.modules.sensorsbackground

import android.os.Bundle
import android.util.Log
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import com.google.gson.Gson;
import expo.modules.sensorsbackground.services.ExpoSensorService
import java.io.Serializable

class ExpoSensorsBackgroundModule : Module() {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ExpoSensorsBackground')` in JavaScript.
        Name("ExpoSensorsBackground")

        // Sets constant properties on the module. Can take a dictionary or a closure that returns a dictionary.
        Constants(
                "PI" to Math.PI
        )

        // Defines event names that the module can send to JavaScript.
        Events("onChange")

        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function("hello") {
            "Hello world! 👋"
        }

        // Defines a JavaScript function that always returns a Promise and whose native code
        // is by default dispatched on the different thread than the JavaScript runtime runs on.
        AsyncFunction("setValueAsync") { value: String ->
            // Send an event to JavaScript.
            sendEvent("onChange", mapOf(
                    "value" to value
            ))
        }
        AsyncFunction("watch") {options: SensorBackgroundOptions ->
            // Send an event to JavaScript.
//            sendEvent("onChange", mapOf(
//                "value" to value
//            ))
            fun mapToBundle(map: Map<String, Any>): Bundle {
                val result = Bundle()
//        if (map == null) return result
                for (key in map.keys) {
                    result.putSerializable(key, map[key] as Serializable?)
                }
                return result
            }
            val mUpdateInteval:Int? = options.timeInterval
            var lastCurrent:Long? = 0;
            val expoServiceSensor = ExpoSensorService()
            expoServiceSensor?.addListener{ data: SensorData ->
                var current = System.currentTimeMillis()

                if((current - lastCurrent!!) > mUpdateInteval!!){
                    sendEvent("onChange", mapOf(
                "x" to data.x,"y" to data.y,"z" to data.z
            ))

                    lastCurrent = current
                }


            }
            expoServiceSensor!!.delaySensor(options.delay!!)
            expoServiceSensor!!.start(appContext.reactContext)
        }


        AsyncFunction("start") { taskName: String, options: SensorBackgroundOptions, promise: Promise ->
            Log.i("ExpoSensors", "start");
            val map: Map<String, Any> = HashMap()
            val gson = Gson();
            val jsonString = gson.toJson(options)
            val taskOptions = Gson().fromJson(jsonString, map.javaClass)
            appContext.taskManager!!.registerTask(taskName, ExpoSensorsBackgroundTaskConsumer::class.java, taskOptions)
            promise.resolve(null)
        }

        AsyncFunction("stop") { taskName: String, promise: Promise ->
            Log.i("ExpoSensors", "stop")
            appContext.taskManager!!.unregisterTask(taskName, ExpoSensorsBackgroundTaskConsumer::class.java)
            promise.resolve(null)
        }

        // Enables the module to be used as a native view. Definition components that are accepted as part of
        // the view definition: Prop, Events.
        View(ExpoSensorsBackgroundView::class) {
            // Defines a setter for the `name` prop.
            Prop("name") { view: ExpoSensorsBackgroundView, prop: String ->
                println(prop)
            }
        }
    }

}
