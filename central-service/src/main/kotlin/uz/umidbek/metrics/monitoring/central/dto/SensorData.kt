package uz.umidbek.metrics.monitoring.central.dto

import uz.umidbek.commons.enums.SensorType

data class SensorData(
    val sensorType: SensorType,
    val sensorId: String,
    val value: Int
)