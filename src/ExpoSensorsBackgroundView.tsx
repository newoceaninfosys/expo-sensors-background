import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ExpoSensorsBackgroundViewProps } from './ExpoSensorsBackground.types';

const NativeView: React.ComponentType<ExpoSensorsBackgroundViewProps> =
  requireNativeViewManager('ExpoSensorsBackground');

export default function ExpoSensorsBackgroundView(props: ExpoSensorsBackgroundViewProps) {
  return <NativeView {...props} />;
}
