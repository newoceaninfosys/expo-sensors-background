import { StyleSheet, Text, View, TouchableOpacity } from "react-native";
import * as TaskManager from "expo-task-manager";
import * as ExpoSensorsBackground from "expo-sensors-background";
import { useEffect, useRef,useState } from "react";

const TASK_NAME = "background-sensor-task";
 // shake api
 const getApiShake = async () => {
  try {
      const response = await fetch(
        `https://f17c-115-75-6-162.ap.ngrok.io`
      );
      const json = await response.text();
      // debounce webview
      return json;
    }
   catch (error) {
    console.log(error);
  }
}

TaskManager.defineTask(TASK_NAME, ({ data, error }) => {
  if (error) {
    // Error occurred - check `error.message` for more details.
    return;
  }
  if (data) {
    // const {x, y, z}: any = data;
    // console.log(x,y,z,"Đate: ",Date.now());
    
    
    // const acceleration = Math.sqrt(x * x + y * y + z * z);
    // const sensibility = 20;
    // if (acceleration >= sensibility) {
    //   console.log(acceleration);
    //   getApiShake();
    //   ToastAndroid.show("Shaking detected", ToastAndroid.SHORT);
    // }
  }
});
export default function App() {
  let removeWatch: any = useRef(null);
  const start = () => {
    ExpoSensorsBackground.start(TASK_NAME, {
      delay: 3,
      timeInterval: 100,
      foregroundService: {
        killServiceOnDestroy: true,
        notificationBody: "App is running....",
        notificationColor: "blue",
        notificationTitle: "Sensors Background",
      },
    });
  }
  const stop = ()=>{
    ExpoSensorsBackground.stop(TASK_NAME);
  };
  const watchAsync = async () => {
    const watch = await ExpoSensorsBackground.watch({
      delay: 3,
      timeInterval: 100,
    });
    removeWatch.current = watch;
    ExpoSensorsBackground.setValueAsync("abc")
  };

  const stopWatchAsync = () => {
    if (removeWatch.current == null) throw new Error("watch does not exist");
    return removeWatch.current.remove();
  };

  useEffect(() => {
    ExpoSensorsBackground.addChangeListener((data)=>{
      console.log(data)
    });
      watchAsync()
    return () => {stopWatchAsync()};
  }, []);

  return (
    <View style={styles.container}>
      <Text>{ExpoSensorsBackground.hello()}</Text>
      <View style={styles.containerButton}>

      <TouchableOpacity onPress={()=> start()} style={styles.button}>
          <Text>Start</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={()=> stop()} style={styles.button}>
          <Text>Stop</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => watchAsync()} style={styles.button}>
          <Text>Watch</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => stopWatchAsync()}
          style={styles.button}
        >
          <Text>Stop Watch</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
  containerButton:{
    alignItems: "center",
    justifyContent: "center",
    flexDirection: 'row'
  },
  button: {
    padding: 10,
    margin: 10,
    backgroundColor: "#00ffff",
  }
});
