package expo.modules.sensorsbackground

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class SensorBackgroundOptions(
    @Field var timeInterval: Int?,
    @Field var foregroundService: ForegroundServiceOption?,
    @Field var delay: Int?,
) : Record



data class ForegroundServiceOption(
    @Field var notificationTitle: String,
    @Field var notificationBody: String,
    @Field var notificationColor: String?,
    @Field var killServiceOnDestroy: Boolean?,
) : Record
data class SensorData(
    @Field var x: Float,
    @Field var y: Float,
    @Field var z: Float,
): Record