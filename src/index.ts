import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';
import { SensorTaskOptions} from './Sensor.types';
// Import the native module. On web, it will be resolved to ExpoSensorsBackground.web.ts
// and on native platforms to ExpoSensorsBackground.ts
import ExpoSensorsBackgroundModule from './ExpoSensorsBackgroundModule';
import ExpoSensorsBackgroundView from './ExpoSensorsBackgroundView';
import { ChangeEventPayload, ExpoSensorsBackgroundViewProps } from './ExpoSensorsBackground.types';

// Get the native constant value.
export const PI = ExpoSensorsBackgroundModule.PI;

export function hello(): string {
  return ExpoSensorsBackgroundModule.hello();
}

export async function setValueAsync(value: string) {
  return await ExpoSensorsBackgroundModule.setValueAsync(value);
}


export async function start(taskName:string, options: SensorTaskOptions = {}) {
  return await ExpoSensorsBackgroundModule.start(taskName,options);
}
export async function stop(taskName:string) {
  return await ExpoSensorsBackgroundModule.stop(taskName);
}


const emitter = new EventEmitter(ExpoSensorsBackgroundModule ?? NativeModulesProxy.ExpoSensorsBackground);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ExpoSensorsBackgroundView, ExpoSensorsBackgroundViewProps, ChangeEventPayload };
