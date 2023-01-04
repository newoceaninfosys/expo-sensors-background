import { StyleSheet, Text, View } from 'react-native';

import * as ExpoSensorsBackground from 'expo-sensors-background';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ExpoSensorsBackground.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
