package expo.modules.sensorsbackground

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class SensorBackgroundOptions(
    @Field val timeInterval: Number?,
    @Field val foregroundService: ForegroundServiceOption?
)

data class ForegroundServiceOption(
    @Field val notificationTitle: String,
    @Field val notificationBody: String,
    @Field val notificationColor: String?,
    @Field val killServiceOnDestroy: Boolean?,
)
