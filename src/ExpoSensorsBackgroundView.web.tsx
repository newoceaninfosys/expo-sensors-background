import * as React from 'react';

import { ExpoSensorsBackgroundViewProps } from './ExpoSensorsBackground.types';

export default function ExpoSensorsBackgroundView(props: ExpoSensorsBackgroundViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
