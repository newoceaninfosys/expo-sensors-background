
// @needsAudit
/**
 * Type representing options argument in `getCurrentPositionAsync`.
 */
export enum SensorDelay {
   
  SENSOR_DELAY_FASTEST = 0,
   
  SENSOR_DELAY_GAME = 1,
   
  SENSOR_DELAY_UI = 2,
   
  SENSOR_DELAY_NORMAL = 3,
}


export type SensorOptions = {
   /**
     * Minimum time to wait between each update in milliseconds.
     * @platform android
     */
    delay?: SensorDelay;
    /**
     * Minimum time to wait between each update in milliseconds.
     * @platform android
     */
    timeInterval?: number;
  };
  
// @needsAudit
/**
 * Type representing background location task options.
 */
export type SensorTaskOptions = SensorOptions & {
    foregroundService?: SensorTaskServiceOptions;
  };
  
  // @needsAudit
  export type SensorTaskServiceOptions = {
    /**
     * Title of the foreground service notification.
     */
    notificationTitle: string;
    /**
     * Subtitle of the foreground service notification.
     */
    notificationBody: string;
    /**
     * Color of the foreground service notification. Accepts `#RRGGBB` and `#AARRGGBB` hex formats.
     */
    notificationColor?: string;
    /**
     * Boolean value whether to destroy the foreground service if the app is killed.
     */
    killServiceOnDestroy?: boolean;
  };