
// @needsAudit
/**
 * Type representing options argument in `getCurrentPositionAsync`.
 */
export type SensorOptions = {
    /**
     * Minimum time to wait between each update in milliseconds.
     * Default value may depend on `accuracy` option.
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