import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';
// Import the native module. On web, it will be resolved to ExpoSensorsBackground.web.ts
// and on native platforms to ExpoSensorsBackground.ts
import ExpoSensorsBackgroundModule from './ExpoSensorsBackgroundModule';
import ExpoSensorsBackgroundView from './ExpoSensorsBackgroundView';
import { ChangeEventPayload, ExpoSensorsBackgroundViewProps, SensorTaskOptions,SensorDelay } from './ExpoSensorsBackground.types';

// Get the native constant value.
export const PI = ExpoSensorsBackgroundModule.PI;

export function hello(): string {
  return ExpoSensorsBackgroundModule.hello();
}

export async function setValueAsync(value: string) {
  return await ExpoSensorsBackgroundModule.setValueAsync(value);
}


export async function start(taskName:string, options: SensorTaskOptions = {}) {
  let cloneOptions = {};
  if(options.delay == undefined){
    cloneOptions = {
      ...options,
      delay: SensorDelay.SENSOR_DELAY_FASTEST,
    }
  }
  else {
    cloneOptions = options
  }
  return await ExpoSensorsBackgroundModule.start(taskName,cloneOptions);
}

export async function stop(taskName:string) {
  return await ExpoSensorsBackgroundModule.stop(taskName);
}

export async function watch(options: SensorTaskOptions = {}) {
  return await ExpoSensorsBackgroundModule.watch(options);
}

const emitter = new EventEmitter(ExpoSensorsBackgroundModule ?? NativeModulesProxy.ExpoSensorsBackground);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ExpoSensorsBackgroundView, ExpoSensorsBackgroundViewProps, ChangeEventPayload };
