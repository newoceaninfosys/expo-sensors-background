import { StyleSheet, Text, View } from "react-native";
import * as TaskManager from "expo-task-manager";
import * as ExpoSensorsBackground from "expo-sensors-background";
import { useEffect } from "react";

const TASK_NAME = "background-sensor-task";

TaskManager.defineTask(TASK_NAME, ({ data, error }) => {
  if (error) {
    // Error occurred - check `error.message` for more details.
    return;
  }
  if (data) {
    console.log(data);
    // do something with the locations captured in the background
  }
});
export default function App() {
  useEffect(() => {
    ExpoSensorsBackground.start(TASK_NAME, {
      timeInterval:100,
      foregroundService: {
        killServiceOnDestroy: true,
        notificationBody: "a",
        notificationColor: "red",
        notificationTitle: "Sensors Background",
      },
    });

    return () => {
      ExpoSensorsBackground.stop(TASK_NAME);
    };
  }, []);
  return (
    <View style={styles.container}>
      <Text>{ExpoSensorsBackground.hello()}</Text>
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
});
