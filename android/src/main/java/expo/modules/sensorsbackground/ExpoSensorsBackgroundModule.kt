package expo.modules.sensorsbackground

import android.util.Log
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.util.Objects

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
  AsyncFunction("start"){ taskName: String, options: Map<String,Object?>, promise: Promise ->
    Log.i("ExpoSensors", "start")
//    var expoServiceSensor = ExpoSensorService();
//    expoServiceSensor.start(appContext.reactContext);
//    var taskOptions: Map<String, Any> = mapOf(
//      "timeInterval" to options.timeInterval!!,
//      "foregroundService" to mapOf(
//        "notificationTitle" to options.foregroundService!!.notificationTitle,
//        "notificationBody" to options.foregroundService.notificationBody,
//        "notificationColor" to options.foregroundService.notificationColor,
//        "killServiceOnDestroy" to options.foregroundService.killServiceOnDestroy,
//      )
//    );
    appContext.taskManager!!.registerTask(taskName, ExpoSensorsBackgroundTaskConsumer::class.java, options)
    promise.resolve(true)
  }

    AsyncFunction("stop"){taskName: String, promise: Promise ->
      Log.i("ExpoSensors", "stop")
      appContext.taskManager!!.unregisterTask(taskName, ExpoSensorsBackgroundTaskConsumer::class.java)
      promise.resolve(true)
    }
    
//    AsyncFunction("registerSensor") {sensorManager promise: Promise ->
//      Log.i("ExpoSensorsBackgroundModule", "register sensor");
//      sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//      senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//      sensorManager.registerListener(listener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//
//      promise.resolve(true);
//    }

    
   
    
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
